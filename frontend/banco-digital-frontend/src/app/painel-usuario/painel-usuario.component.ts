import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Importar FormsModule
import { UsuarioService } from '../usuario.service';
import { EmprestimoService } from './emprestimo.service'; // Importar EmprestimoService
import { jwtDecode } from 'jwt-decode';
import { Usuario } from '../models/usuario.model';
import { Emprestimo, EmprestimoSolicitacaoDTO } from '../models/emprestimo.model';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs'; // Importar Subscription

@Component({
  selector: 'app-painel-usuario',
  standalone: true,
  imports: [CommonModule, DatePipe, CurrencyPipe, FormsModule], // Adicionar FormsModule
  templateUrl: './painel-usuario.component.html',
  styleUrls: ['./painel-usuario.component.css']
})
export class PainelUsuarioComponent implements OnInit {
  usuario: Usuario | null = null;
  showLoanForm: boolean = false;
  valorEmprestimo: number = 0;
  parcelasEmprestimo: number = 0;
  valorTotalSimulado: number = 0;
  valorParcelaSimulada: number = 0;
  jurosAplicadosSimulados: number = 0.01; // 1% ao mês
  emprestimos: Emprestimo[] = [];
  mensagemEmprestimo: string = '';
  erroEmprestimo: string = '';
  showConfirmacaoEmprestimoPopup: boolean = false; // Nova variável para controlar o pop-up
  private emprestimosSubscription: Subscription | undefined; // Adicionar Subscription

  constructor(
    private usuarioService: UsuarioService,
    private emprestimoService: EmprestimoService, // Injetar EmprestimoService
    private router: Router
  ) {}

  ngOnInit(): void {
    const token = sessionStorage.getItem('authToken');
    if (token) {
      const decodedToken: { sub: string } = jwtDecode(token);
      console.log('Token decodificado, CPF:', decodedToken.sub);
      this.carregarDadosCliente();
      this.carregarMeusEmprestimos();
      this.subscribeToEmprestimoUpdates(); // Chamar o método de subscrição
    } else {
      console.warn('Nenhum token JWT encontrado no sessionStorage.');
      this.router.navigate(['/login']);
    }
  }

  ngOnDestroy(): void { // Adicionar ngOnDestroy para desinscrever
    if (this.emprestimosSubscription) {
      this.emprestimosSubscription.unsubscribe();
    }
  }

  private subscribeToEmprestimoUpdates(): void {
    this.emprestimosSubscription = this.emprestimoService.emprestimosAtualizacoes$.subscribe(
      (emprestimos: Emprestimo[]) => {
        // Filtrar empréstimos para mostrar apenas os do usuário logado
        const token = sessionStorage.getItem('authToken');
        if (token) {
          const decodedToken: { sub: string } = jwtDecode(token);
          const cpfUsuarioLogado = decodedToken.sub;
          this.emprestimos = emprestimos.filter(e => e.usuario.cpf === cpfUsuarioLogado).map(emprestimo => ({
            ...emprestimo,
            valorTotal: emprestimo.valorTotal ? Number(emprestimo.valorTotal) : undefined,
            valorParcela: emprestimo.valorParcela ? Number(emprestimo.valorParcela) : undefined
          }));
          console.log('Empréstimos atualizados via WebSocket para o usuário:', this.emprestimos);
          this.carregarDadosCliente(); // Recarrega os dados do cliente para atualizar o saldo
        }
      }
    );
  }

  carregarDadosCliente(): void {
    this.usuarioService.getDadosUsuarioAutenticado().subscribe({
      next: (data: Usuario) => {
        console.log('Dados do usuário recebidos do backend:', data); // Log dos dados do usuário
        this.usuario = {
          ...data,
          saldo: Number(data.saldo),
          limiteCredito: Number(data.limiteCredito)
        };

        // Bloquear acesso se o status não for ATIVO
        if (this.usuario.status !== 'ATIVO') {
          this.router.navigate(['/aguardando-analise']); // Redireciona para a página de aguardando análise
        }
      },
      error: (error: Error) => {
        console.error('Erro ao carregar dados do usuário:', error);
        this.router.navigate(['/login']); // Redireciona para login em caso de erro
      }
    });
  }

  simularEmprestimo(): void {
    this.valorTotalSimulado = 0;
    this.valorParcelaSimulada = 0;
    this.erroEmprestimo = '';
    console.log('Simulando empréstimo - Valor:', this.valorEmprestimo, 'Parcelas:', this.parcelasEmprestimo); // Log de entrada da simulação

    if (this.valorEmprestimo < 100 || this.valorEmprestimo > 10000) {
      this.erroEmprestimo = 'O valor do empréstimo deve ser entre R$100,00 e R$10.000,00.';
      return;
    }

    if (this.parcelasEmprestimo <= 0 || this.parcelasEmprestimo > 24) {
      this.erroEmprestimo = 'O número de parcelas deve ser entre 1 e 24.';
      return;
    }

    this.valorTotalSimulado = this.valorEmprestimo * (1 + this.jurosAplicadosSimulados * this.parcelasEmprestimo);
    this.valorParcelaSimulada = this.valorTotalSimulado / this.parcelasEmprestimo;
    console.log('Resultado da simulação - Total:', this.valorTotalSimulado, 'Parcela:', this.valorParcelaSimulada); // Log do resultado da simulação
  }

  abrirConfirmacaoEmprestimo(): void {
    this.mensagemEmprestimo = '';
    this.erroEmprestimo = '';
    this.simularEmprestimo(); // Garante que os valores simulados estejam atualizados
    if (!this.erroEmprestimo) {
      this.showConfirmacaoEmprestimoPopup = true;
    }
  }

  cancelarSolicitacao(): void {
    this.showConfirmacaoEmprestimoPopup = false;
  }

  confirmarSolicitacaoEmprestimo(): void {
    this.showConfirmacaoEmprestimoPopup = false; // Fecha o pop-up
    this.enviarSolicitacaoEmprestimo(); // Chama o método que realmente envia a solicitação
  }

  enviarSolicitacaoEmprestimo(): void {
    this.mensagemEmprestimo = '';
    this.erroEmprestimo = '';

    this.simularEmprestimo(); // Garante que os valores simulados estejam atualizados

    if (this.erroEmprestimo) { // Se houver erro na simulação, não prossegue
      return;
    }

    const token = sessionStorage.getItem('authToken'); // Alterado para sessionStorage
    if (!token) {
      this.erroEmprestimo = 'Token de autenticação não encontrado.';
      return;
    }
    const decodedToken: { sub: string } = jwtDecode(token);
    // const clienteCpf: string = decodedToken.sub; // Não é mais necessário, o backend pega do token

    const emprestimoData: EmprestimoSolicitacaoDTO = {
      valor: this.valorEmprestimo,
      parcelas: this.parcelasEmprestimo
    };
    console.log('Dados do empréstimo a serem enviados:', emprestimoData);
    this.emprestimoService.solicitarEmprestimo(emprestimoData).subscribe({ // Chama o serviço de empréstimo
      next: (response: Emprestimo) => {
        console.log('Resposta da solicitação de empréstimo:', response);
        this.mensagemEmprestimo = 'Solicitação de empréstimo enviada com sucesso! Aguardando aprovação.';
        this.valorEmprestimo = 0;
        this.parcelasEmprestimo = 0;
        this.valorTotalSimulado = 0;
        this.valorParcelaSimulada = 0;
        if (response.valorTotal) {
          response.valorTotal = Number(response.valorTotal);
        }
        if (response.valorParcela) {
          response.valorParcela = Number(response.valorParcela);
        }
        this.carregarMeusEmprestimos();
        if (this.usuario) {
          this.carregarDadosCliente();
        }
      },
      error: (error) => {
        console.error('Erro ao solicitar empréstimo:', error);
        if (error.error && error.error.message) {
          this.erroEmprestimo = error.error.message;
        } else {
          this.erroEmprestimo = 'Erro ao solicitar empréstimo. Tente novamente.';
        }
      }
    });
  }

  carregarMeusEmprestimos(): void {
    this.usuarioService.getMeusEmprestimos().subscribe({
      next: (data: Emprestimo[]) => {
        console.log('Empréstimos carregados:', data); // Log dos empréstimos
        this.emprestimos = data.map(emprestimo => ({
          ...emprestimo,
          valorTotal: emprestimo.valorTotal ? Number(emprestimo.valorTotal) : undefined,
          valorParcela: emprestimo.valorParcela ? Number(emprestimo.valorParcela) : undefined
        }));
      },
      error: (error: Error) => {
        console.error('Erro ao carregar empréstimos:', error);
        // Tratar erro, talvez exibir mensagem
      }
    });
  }

  logout(): void {
    sessionStorage.removeItem('authToken'); // Alterado para sessionStorage
    sessionStorage.removeItem('userRole');
    sessionStorage.removeItem('cpf');
    sessionStorage.removeItem('email');
    sessionStorage.removeItem('nome');
    sessionStorage.removeItem('status');
    sessionStorage.removeItem('papel');
    this.router.navigate(['/login']);
  }
}





