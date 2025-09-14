import { Component, OnInit, OnDestroy } from '@angular/core'; // Forçando recompilação
import { CommonModule, DecimalPipe } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms'; // Importação necessária para ngModel
import { UsuarioService } from '../usuario.service';
import { EmprestimoService } from '../painel-usuario/emprestimo.service'; // Importar EmprestimoService
import { Usuario } from '../models/usuario.model';
import { Emprestimo } from '../models/emprestimo.model';
import { Subscription, forkJoin } from 'rxjs'; // Importar forkJoin
import { Chart, ChartConfiguration, ChartData, ChartType, registerables } from 'chart.js'; // Importar tipos do Chart.js
import { BaseChartDirective } from 'ng2-charts'; // Importar BaseChartDirective

@Component({
  selector: 'app-painel-gerente',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective, DecimalPipe], // Adicionado FormsModule, BaseChartDirective e DecimalPipe
  templateUrl: './painel-gerente.component.html',
  styleUrls: ['./painel-gerente.component.css']
})
export class PainelGerenteComponent implements OnInit, OnDestroy {
  todosUsuarios: Usuario[] = [];
  usuariosFiltrados: Usuario[] = [];
  emprestimosFiltrados: Emprestimo[] = [];
  todosEmprestimos: Emprestimo[] = [];
  filtroStatus: string = 'TODOS'; // Inicia mostrando todos os usuários (PENDENTE e ATIVO)
  filtroStatusEmprestimo: string = 'TODOS'; // Inicia mostrando todos os empréstimos
  filtroNomeUsername: string = '';
  viewMode: 'usuarios' | 'emprestimos' | 'dashboard' | 'configuracoes' = 'dashboard'; // Controla a visualização
  mensagemSucesso: string | null = null;
  mensagemErro: string | null = null;
  totalUsuarios: number = 0; // Nova propriedade para a contagem de usuários
  novaSenha: string = '';
  confirmarSenha: string = '';
  private emprestimosPendentesSubscription: Subscription | undefined; // Adicionar Subscription para empréstimos pendentes
  private emprestimosAtualizacoesSubscription: Subscription | undefined; // Adicionar Subscription para atualizações gerais

  // Configurações do gráfico
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        grid: {
          color: 'rgba(255,255,255,0.1)'
        },
        ticks: {
          color: '#e0e0e0'
        }
      },
      y: {
        beginAtZero: true,
        grid: {
          color: 'rgba(255,255,255,0.1)'
        },
        ticks: {
          color: '#e0e0e0',
          stepSize: 1
        }
      }
    },
    plugins: {
      legend: {
        display: true,
        labels: {
          color: '#e0e0e0'
        }
      },
      tooltip: {
        backgroundColor: 'rgba(0,0,0,0.7)',
        bodyColor: '#e0e0e0',
        titleColor: '#9f7aea'
      }
    }
  };
  public barChartType: ChartType = 'bar';
  public barChartData: ChartData<'bar'> = {
    labels: ['Pendentes', 'Aprovados', 'Negados'],
    datasets: [
      { data: [], label: 'Empréstimos por Status', backgroundColor: ['#ffc107', '#28a745', '#dc3545'] }
    ]
  };

  private subscriptions = new Subscription();

  constructor(
    private usuarioService: UsuarioService,
    private emprestimoService: EmprestimoService, // Injetar EmprestimoService
    private router: Router
  ) {}

  ngOnInit(): void {
    Chart.register(...registerables);
    this.carregarTodosUsuarios();
    this.carregarEmprestimos();
    this.carregarContagemUsuarios();
    this.carregarDadosGraficoEmprestimos();
    this.subscribeToEmprestimoUpdates(); // Chamar o método de subscrição
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    if (this.emprestimosPendentesSubscription) {
      this.emprestimosPendentesSubscription.unsubscribe();
    }
    if (this.emprestimosAtualizacoesSubscription) {
      this.emprestimosAtualizacoesSubscription.unsubscribe();
    }
  }

  private subscribeToEmprestimoUpdates(): void {
    this.emprestimosPendentesSubscription = this.emprestimoService.emprestimosPendentes$.subscribe(
      (emprestimos: Emprestimo[]) => {
        this.todosEmprestimos = emprestimos;
        this.aplicarFiltrosEmprestimos();
        this.carregarDadosGraficoEmprestimos(); // Atualizar gráfico ao receber novos empréstimos pendentes
        console.log('Empréstimos pendentes atualizados via WebSocket:', emprestimos);
      }
    );

    this.emprestimosAtualizacoesSubscription = this.emprestimoService.emprestimosAtualizacoes$.subscribe(
      (emprestimos: Emprestimo[]) => {
        // Atualizar a lista de todos os empréstimos com base nas atualizações
        this.todosEmprestimos = emprestimos;
        this.aplicarFiltrosEmprestimos();
        this.carregarDadosGraficoEmprestimos(); // Atualizar gráfico ao receber atualizações
        console.log('Empréstimos atualizados via WebSocket:', emprestimos);
      }
    );
  }

  carregarTodosUsuarios(): void {
    const sub = this.usuarioService.getTodosUsuarios().subscribe({
      next: (usuarios) => {
        this.todosUsuarios = usuarios;
        this.aplicarFiltros();
        console.log('Todos os usuários carregados:', usuarios);
      },
      error: (err: any) => {
        console.error('Erro ao carregar todos os usuários', err);
      }
    });
    this.subscriptions.add(sub);
  }

  carregarEmprestimos(): void {
    const sub = this.emprestimoService.listarTodosEmprestimos().subscribe({
      next: (emprestimos: Emprestimo[]) => {
        this.todosEmprestimos = emprestimos;
        this.aplicarFiltrosEmprestimos();
        console.log('Todos os empréstimos carregados:', emprestimos);
      },
      error: (err: any) => {
        console.error('Erro ao carregar empréstimos', err);
      }
    });
    this.subscriptions.add(sub);
  }

  carregarContagemUsuarios(): void {
    const sub = this.usuarioService.getContagemUsuarios().subscribe({
      next: (count: number) => {
        this.totalUsuarios = count;
        console.log('Contagem total de usuários:', count);
      },
      error: (err: any) => {
        console.error('Erro ao carregar contagem de usuários', err);
      }
    });
    this.subscriptions.add(sub);
  }

  carregarDadosGraficoEmprestimos(): void {
    forkJoin([
      this.emprestimoService.getEmprestimosCountByStatus('PENDENTE'),
      this.emprestimoService.getEmprestimosCountByStatus('APROVADO'),
      this.emprestimoService.getEmprestimosCountByStatus('NEGADO')
    ]).subscribe({
      next: ([pendentes, aprovados, negados]) => {
       this.barChartData.datasets[0].data = [pendentes, aprovados, negados];
        this.barChartData = { ...this.barChartData };
      },
      error: (err: any) => {
        console.error('Erro ao carregar dados do gráfico de empréstimos', err);
      }
    });
  }

  aplicarFiltros(): void {
    let usuarios = this.todosUsuarios;

    // 1. Filtro por Status
    if (this.filtroStatus && this.filtroStatus !== 'TODOS') {
      usuarios = usuarios.filter(u => u.status === this.filtroStatus);
    }

    // 2. Filtro por Nome/CPF
    if (this.filtroNomeUsername) {
      const filtro = this.filtroNomeUsername.toLowerCase();
      usuarios = usuarios.filter(u =>
        u.nome.toLowerCase().includes(filtro) ||
        (u.papel === 'ROLE_CLIENTE' && u.cpf.toLowerCase().includes(filtro)) // Busca por CPF para clientes
      );
    }

    this.usuariosFiltrados = usuarios;
  }

  aprovarCadastro(id: number): void {
    const sub = this.usuarioService.aprovarUsuario(id).subscribe({
      next: (usuarioAprovado: Usuario) => {
        console.log('Usuário aprovado:', usuarioAprovado);
        const index = this.todosUsuarios.findIndex(u => u.id === id);
        if (index > -1) {
          this.todosUsuarios[index].status = 'ATIVO';
        }
        this.aplicarFiltros();
        this.exibirMensagem('Usuário aprovado com sucesso!', 'sucesso');
      },
      error: (err: any) => {
        console.error(`Erro ao aprovar usuário ${id}`, err);
        this.exibirMensagem(`Erro ao aprovar usuário ${id}: ${err.message || err.error?.message || 'Erro desconhecido'}`, 'erro');
      }
    });
    this.subscriptions.add(sub);
  }

  reprovarCadastro(id: number): void {
    const sub = this.usuarioService.reprovarUsuario(id).subscribe({
      next: (usuarioReprovado: Usuario) => {
        console.log('Usuário reprovado:', usuarioReprovado);
        const index = this.todosUsuarios.findIndex(u => u.id === Number(id));
        if (index > -1) {
          this.todosUsuarios[index].status = 'REPROVADO';
        }
        this.aplicarFiltros();
        this.exibirMensagem('Usuário reprovado com sucesso!', 'sucesso');
      },
      error: (err: any) => {
        console.error(`Erro ao reprovar usuário ${id}`, err);
        this.exibirMensagem(`Erro ao reprovar usuário ${id}: ${err.message || err.error?.message || 'Erro desconhecido'}`, 'erro');
      }
    });
    this.subscriptions.add(sub);
  }

  exibirMensagem(mensagem: string, tipo: 'sucesso' | 'erro'): void {
    if (tipo === 'sucesso') {
      this.mensagemSucesso = mensagem;
      this.mensagemErro = null;
    } else {
      this.mensagemErro = mensagem;
      this.mensagemSucesso = null;
    }
    setTimeout(() => {
      this.mensagemSucesso = null;
      this.mensagemErro = null;
    }, 5000); // Mensagem desaparece após 5 segundos
  }

  aprovarEmprestimo(id: number): void {
    const sub = this.emprestimoService.aprovarEmprestimo(id).subscribe({
      next: () => {
        console.log(`Empréstimo ${id} aprovado com sucesso.`);
        this.exibirMensagem(`Empréstimo ${id} aprovado com sucesso!`, 'sucesso');
        this.carregarEmprestimos();
        this.carregarDadosGraficoEmprestimos();
      },
      error: (err: any) => {
        console.error(`Erro ao aprovar empréstimo ${id}`, err);
        this.exibirMensagem(`Erro ao aprovar empréstimo ${id}: ${err.message || err.error?.message || 'Erro desconhecido'}`, 'erro');
      }
    });
    this.subscriptions.add(sub);
  }

  trocarSenha(): void {
    if (this.novaSenha !== this.confirmarSenha) {
      this.exibirMensagem('As senhas não coincidem.', 'erro');
      return;
    }

    if (this.novaSenha.length < 6) {
      this.exibirMensagem('A nova senha deve ter no mínimo 6 caracteres.', 'erro');
      return;
    }

    const emailGerente = this.usuarioService.getEmail(); // Obter o email do gerente logado
    if (!emailGerente) {
      this.exibirMensagem('Não foi possível identificar o gerente logado para trocar a senha.', 'erro');
      return;
    }

    this.usuarioService.trocarSenhaGerente(emailGerente, this.novaSenha).subscribe({
      next: () => {
        this.exibirMensagem('Senha alterada com sucesso!', 'sucesso');
        this.novaSenha = '';
        this.confirmarSenha = '';
      },
      error: (err: any) => {
        console.error('Erro ao trocar senha:', err);
        this.exibirMensagem(`Erro ao trocar senha: ${err.error?.message || 'Erro desconhecido'}`, 'erro');
      }
    });
  }

  reprovarEmprestimo(id: number): void {
    const sub = this.emprestimoService.reprovarEmprestimo(id).subscribe({
      next: () => {
        console.log(`Empréstimo ${id} reprovado com sucesso.`);
        this.exibirMensagem(`Empréstimo ${id} reprovado com sucesso!`, 'sucesso');
        this.carregarEmprestimos();
        this.carregarDadosGraficoEmprestimos();
      },
      error: (err: any) => {
        console.error(`Erro ao reprovar empréstimo ${id}`, err);
        this.exibirMensagem(`Erro ao reprovar empréstimo ${id}: ${err.message || err.error?.message || 'Erro desconhecido'}`, 'erro');
      }
    });
    this.subscriptions.add(sub);
  }

  aplicarFiltrosEmprestimos(): void {
    let emprestimos = this.todosEmprestimos;

    if (this.filtroStatusEmprestimo && this.filtroStatusEmprestimo !== 'TODOS') {
      emprestimos = emprestimos.filter(e => e.status === this.filtroStatusEmprestimo);
    }

    this.emprestimosFiltrados = emprestimos;
  }

  logout(): void {
    this.usuarioService.logout(); // Usar o método de logout do serviço
    this.router.navigate(['/login']);
  }

  getEmprestimoStatus(status: string): string {
    switch (status) {
      case 'PENDENTE':
        return 'Em Análise';
      case 'APROVADO':
        return 'Aprovado';
      case 'NEGADO':
        return 'Negado';
      default:
        return status;
    }
  }

  getEmprestimoStatusClass(status: string): string {
    switch (status) {
      case 'PENDENTE':
        return 'status-pendente';
      case 'APROVADO':
        return 'status-aprovado';
      case 'NEGADO':
        return 'status-rejeitado'; // Mantido 'status-rejeitado' para estilo CSS
      default:
        return '';
    }
  }
  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDENTE':
        return 'status-pendente';
      case 'ATIVO':
        return 'status-aprovado';
      case 'RECUSADO':
        return 'status-rejeitado';
      default:
        return '';
    }
  }

  private exportToPdf(data: any[], columns: string[], title: string, filename: string): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.default();
        doc.text(title, 14, 16);
        (doc as any).autoTable({
          head: [columns],
          body: data,
          startY: 20,
          styles: {
            fontSize: 10,
            cellPadding: 3,
            fillColor: '#343a40',
            textColor: 255,
            lineColor: '#495057',
            lineWidth: 0.1
          },
          headStyles: {
            fillColor: '#6f42c1',
            textColor: 255,
            fontStyle: 'bold'
          },
          alternateRowStyles: {
            fillColor: '#495057'
          },
        });
        doc.save(filename);
      });
    });
  }

  private exportToExcel(data: any[], filename: string, sheetName: string): void {
    import('xlsx').then(xlsx => {
      const ws = xlsx.utils.json_to_sheet(data);
      const wb = xlsx.utils.book_new();
      xlsx.utils.book_append_sheet(wb, ws, sheetName);
      xlsx.writeFile(wb, filename);
    });
  }

  exportarUsuariosParaPdf(): void {
    const columns = ['Nome', 'CPF', 'E-mail', 'Telefone', 'Status'];
    const rows = this.usuariosFiltrados.map(u => [u.nome, u.cpf, u.email, u.telefone, u.status]);
    this.exportToPdf(rows, columns, 'Relatório de Usuários', 'usuarios.pdf');
  }

  exportarUsuariosParaExcel(): void {
    const data = this.usuariosFiltrados.map(u => ({
      Nome: u.nome,
      CPF: u.cpf,
      'E-mail': u.email,
      Telefone: u.telefone,
      Status: u.status
    }));
    this.exportToExcel(data, 'usuarios.xlsx', 'Usuários');
  }

  exportarEmprestimosParaPdf(): void {
    const columns = ['ID Empréstimo', 'CPF Usuário', 'Valor', 'Status'];
    const dataToExport = this.emprestimosFiltrados;
    const rows = dataToExport.map(e => [e.id, e.usuario.cpf, e.valor, e.status]);
    this.exportToPdf(rows, columns, 'Relatório de Empréstimos', 'emprestimos.pdf');
  }

  exportarEmprestimosParaExcel(): void {
    const dataToExport = this.emprestimosFiltrados;
    const data = dataToExport.map(e => ({
      'ID Empréstimo': e.id,
      'CPF Usuário': e.usuario.cpf,
      Valor: e.valor,
      Status: e.status
    }));
    this.exportToExcel(data, 'emprestimos.xlsx', 'Empréstimos');
  }
}