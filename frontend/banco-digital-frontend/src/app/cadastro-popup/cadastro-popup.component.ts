import { Component, ElementRef, ViewChild, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UsuarioService } from '../usuario.service';
import { Usuario } from '../models/usuario.model';

@Component({
  selector: 'app-cadastro-popup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], // Adicione ReactiveFormsModule
  templateUrl: './cadastro-popup.component.html',
  styleUrls: ['./cadastro-popup.component.scss']
})
export class CadastroPopupComponent implements OnInit {
  @ViewChild('popup') popup!: ElementRef;
  @Input() showPopup: boolean = false;
  @Output() close = new EventEmitter<void>();

  cadastroForm: FormGroup;
  currentStep: number = 1;
  errorMessage: string = '';
  showSuccessPopup: boolean = false;
  transformStyle: string = 'translateX(0%)';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private usuarioService: UsuarioService
  ) {
    this.cadastroForm = this.fb.group({
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]], // CPF com 11 dígitos
      nome: ['', [Validators.required]],
      endereco: ['', [Validators.required]],
      telefone: ['', [Validators.required, Validators.pattern(/^\d{10,11}$/)]], // Telefone com 10 ou 11 dígitos
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      confirmarSenha: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    return form.get('senha')?.value === form.get('confirmarSenha')?.value
      ? null : { 'mismatch': true };
  }

  ngOnInit(): void {
    // Não há lógica de inicialização específica para o pop-up aqui
  }

  nextStep(): void {
    if (this.currentStep === 1 && this.cadastroForm.get('cpf')?.valid) {
      this.currentStep = 2;
    } else if (this.currentStep === 2 && this.cadastroForm.get('nome')?.valid && this.cadastroForm.get('endereco')?.valid && this.cadastroForm.get('telefone')?.valid && this.cadastroForm.get('email')?.valid) {
      this.currentStep = 3;
    }
    this.updateTransform();
  }

  prevStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
    this.updateTransform();
  }

  updateTransform(): void {
    const percentage = -(100 / 3) * (this.currentStep - 1);
    this.transformStyle = `translateX(${percentage}%)`;
  }

  submitCadastro(): void {
    if (this.cadastroForm.invalid) {
      if (this.cadastroForm.errors?.['mismatch']) {
        this.errorMessage = 'As senhas não coincidem.';
      } else {
        this.errorMessage = 'Por favor, preencha todos os campos corretamente.';
      }
      return;
    }

    const novoUsuario: Partial<Usuario> = {
      cpf: this.cadastroForm.value.cpf, // Envia CPF
      nome: this.cadastroForm.value.nome,
      endereco: this.cadastroForm.value.endereco,
      telefone: this.cadastroForm.value.telefone, // Adiciona telefone
      email: this.cadastroForm.value.email,
      senha: this.cadastroForm.value.senha,
      papel: 'ROLE_CLIENTE', // Define o papel como cliente
      status: 'PENDENTE' // Define o status inicial como pendente, consistente com o backend
    };

    this.usuarioService.cadastrarUsuario(novoUsuario).subscribe({
      next: response => {
        this.showSuccessPopup = true;
        this.errorMessage = '';
        // Redireciona para a página de aguardando análise após um pequeno delay para o usuário ver a mensagem de sucesso
        setTimeout(() => {
          this.closeSuccessPopup();
        }, 2000); // 2 segundos de delay
      },
      error: error => {
        this.errorMessage = 'Erro ao cadastrar. Tente novamente.';
        console.error('Erro no cadastro:', error);
      }
    });
  }

  closePopup(): void {
    this.showPopup = false;
    this.close.emit(); // Emite evento para o componente pai fechar o pop-up
    this.resetForm();
  }

  closeSuccessPopup(): void {
    this.showSuccessPopup = false;
    this.closePopup(); // Fecha o pop-up principal após o sucesso
    this.router.navigate(['/aguardando-analise']); // Redireciona para a página de aguardando análise
  }

  resetForm(): void {
    this.cadastroForm.reset();
    this.currentStep = 1;
    this.errorMessage = '';
  }
}