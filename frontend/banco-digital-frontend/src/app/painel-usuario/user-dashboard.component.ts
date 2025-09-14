import { Component, OnInit } from '@angular/core'; // Forçando recompilação
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Router } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { UsuarioService } from '../usuario.service';
import { EmprestimoService } from './emprestimo.service'; // Caminho corrigido para o mesmo diretório
import { Usuario } from '../models/usuario.model';
import { Emprestimo, EmprestimoRequest } from '../models/emprestimo.model';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DecimalPipe],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.scss']
})
export class UserDashboardComponent implements OnInit {
  loanForm: FormGroup;
  simulation: any = null;
  showLoanForm = false;
  saldo: number = 0;
  nomeUsuario: string = '';
  limiteCredito: number = 0;
  emprestimoAtivo: boolean = false;
  ultimaMovimentacao: string = '';
  mensagemAprovacao: string = '';
  isLoggedIn: boolean = false;
  meusEmprestimos: Emprestimo[] = []; // Adicionado para armazenar os empréstimos do usuário

  showBalanceModal: boolean = false;
  showCreditLimitModal: boolean = false;
  showStatementModal: boolean = false;
  showCardsModal: boolean = false;
  showSettingsModal: boolean = false;
  showConfirmationModal: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private usuarioService: UsuarioService,
    private emprestimoService: EmprestimoService
  ) {
    this.loanForm = this.fb.group({
      value: [100, [Validators.required, Validators.min(100), Validators.max(10000)]], // Valor inicial 100
      parcels: [1, [Validators.required, Validators.min(1), Validators.max(24)]], // Valor inicial 1
      purpose: ['']
    });

    // Observar mudanças nos campos de valor e parcelas para simulação em tempo real
    this.loanForm.valueChanges.pipe(
      debounceTime(300), // Espera 300ms após a última digitação
      distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)) // Só emite se houver mudança real
    ).subscribe(() => {
      this.simulateLoan();
    });
  }

  ngOnInit() {
    if (this.usuarioService.isLoggedIn()) {
      this.isLoggedIn = true;
      // Os dados do usuário já estão no sessionStorage após o login
      this.nomeUsuario = this.usuarioService.getNome() || '';
      this.carregarSaldo(); // Carregar saldo do usuário
      this.carregarMeusEmprestimos(); // Carregar empréstimos após obter os dados do usuário
    } else {
      this.router.navigate(['/']);
    }
  }

  carregarSaldo(): void {
    this.usuarioService.getDadosUsuarioAutenticado().subscribe({
      next: (usuario: Usuario) => {
        this.saldo = Number(usuario.saldo);
        this.limiteCredito = Number(usuario.limiteCredito);
        this.nomeUsuario = usuario.nome;
        console.log('Dados do usuário carregados:', usuario);
      },
      error: (err: any) => {
        console.error('Erro ao carregar saldo', err);
      }
    });
  }

  carregarMeusEmprestimos(): void {
    this.emprestimoService.getMeusEmprestimos().subscribe({
      next: (emprestimos: Emprestimo[]) => {
        this.meusEmprestimos = emprestimos;
        // Verifica se há algum empréstimo aprovado ou pendente
        this.emprestimoAtivo = emprestimos.some(e => e.status === 'APROVADO' || e.status === 'PENDENTE');
        console.log('Meus empréstimos carregados:', emprestimos);
      },
      error: (err: any) => {
        console.error('Erro ao carregar meus empréstimos', err);
      }
    });
  }

  openBalanceModal() {
    this.showBalanceModal = true;
  }

  closeBalanceModal() {
    this.showBalanceModal = false;
  }

  openCreditLimitModal() {
    this.showCreditLimitModal = true;
  }

  closeCreditLimitModal() {
    this.showCreditLimitModal = false;
  }

  openStatementModal() {
    this.showStatementModal = true;
  }

  closeStatementModal() {
    this.showStatementModal = false;
  }

  openCardsModal() {
    this.showCardsModal = true;
  }

  closeCardsModal() {
    this.showCardsModal = false;
  }

  openSettingsModal() {
    this.showSettingsModal = true;
  }

  closeSettingsModal() {
    this.showSettingsModal = false;
  }

  openLoanModal() {
    this.showLoanForm = true;
  }

  closeLoanModal() {
    this.showLoanForm = false;
    this.simulation = null;
    this.loanForm.reset();
  }

  simulateLoan() {
    if (this.loanForm.valid) {
      const value = this.loanForm.get('value')?.value;
      const parcels = this.loanForm.get('parcels')?.value;
      const purpose = this.loanForm.get('purpose')?.value;

      if (value > 0 && parcels > 0) {
        const interestRate = 0.01; // 1% de juros
        const interest = value * interestRate;
        const total = value + interest;
        const parcelValue = total / parcels;

        this.simulation = { value, parcels, interest, total, parcelValue, purpose };
      } else {
        this.simulation = null;
      }

    }
  }

  openConfirmationModal() {
    this.showConfirmationModal = true;
  }

  closeConfirmationModal() {
    this.showConfirmationModal = false;
  }

  requestLoan() {
    if (this.simulation) {
      const novoEmprestimo: EmprestimoRequest = {
        valor: this.simulation.value,
        parcelas: this.simulation.parcels,
      };

      this.emprestimoService.solicitarEmprestimo(novoEmprestimo).subscribe({
        next: (response: Emprestimo) => {
          this.mensagemAprovacao = 'Solicitação de empréstimo enviada para análise do gerente.';
          this.showLoanForm = false;
          this.showConfirmationModal = false;
          this.emprestimoAtivo = true;
          this.simulation = null;
          this.loanForm.reset();
          this.carregarMeusEmprestimos(); // Recarregar a lista de empréstimos do usuário
          this.carregarSaldo(); // Recarregar o saldo do usuário
        },
        error: (error: any) => {
          console.error('Erro ao solicitar empréstimo:', error);
          this.mensagemAprovacao = 'Erro ao solicitar empréstimo. Tente novamente.';
          this.showConfirmationModal = false;
        }
      });
    }
  }

  getEmprestimoStatus(status: string): string {
    switch (status) {
      case 'PENDENTE':
        return 'Em Análise';
      case 'APROVADO':
        return 'Aprovado';
      case 'REJEITADO':
        return 'Rejeitado';
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
      case 'REJEITADO':
        return 'status-rejeitado';
      default:
        return '';
    }
  }


  
  logout(): void {
    this.usuarioService.logout();
    this.router.navigate(['/']);
  }
}