import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UsuarioService } from '../usuario.service';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule]
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string = '';
  showPassword = false;
  loginType: 'cliente' | 'gerente' = 'cliente'; // 'cliente' é o padrão

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private usuarioService: UsuarioService
  ) {
    this.loginForm = this.fb.group({
      identifier: ['', [Validators.required]], // Campo único para CPF ou nome de gerente
      senha: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.setLoginType('cliente'); // Garante que o formulário comece no modo cliente
  }

  setLoginType(type: 'cliente' | 'gerente'): void {
    this.loginType = type;
    this.errorMessage = '';
    this.loginForm.reset();

    // A validação do formato do username (CPF ou nome) será feita no backend.
    // Aqui, apenas garantimos que o campo não esteja vazio.
    this.loginForm.get('identifier')?.setValidators([Validators.required]);
    this.loginForm.get('identifier')?.updateValueAndValidity();
  }

  login(): void {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos corretamente.';
      return;
    }

    const identifier = this.loginForm.value.identifier;
    const senha = this.loginForm.value.senha;
    this.usuarioService.login(identifier, senha).subscribe({
      next: response => {
        // O response do login já contém os dados do usuário autenticado
        // Mapear a resposta do backend (AuthResponse) para a interface Usuario
        const usuarioData: Usuario = {
          id: 0, // O ID pode ser obtido de outra forma ou não ser necessário no frontend para o login
          nome: response.nome,
          cpf: response.cpf,
          endereco: '', // O AuthResponse não retorna o endereço, então deixamos vazio ou buscamos separadamente
          email: response.email,
          senha: '', // A senha nunca deve ser retornada ou armazenada no frontend
          papel: response.papel,
          status: response.status,
          saldo: 0, // O AuthResponse não retorna o saldo, então deixamos 0 ou buscamos separadamente
          limiteCredito: 0, // O AuthResponse não retorna o limite de crédito, então deixamos 0 ou buscamos separadamente
          situacaoCredito: 'PENDENTE', // O AuthResponse não retorna a situação de crédito, então deixamos PENDENTE ou buscamos separadamente
          token: response.token
        };
        
        if (usuarioData.status === 'PENDENTE' && usuarioData.papel === 'ROLE_CLIENTE') {
          this.router.navigate(['/aguardando-analise']);
        } else if (usuarioData.papel === 'ROLE_GERENTE') {
          this.router.navigate(['/painel-gerente']);
        } else if (usuarioData.papel === 'ROLE_CLIENTE' && usuarioData.status === 'ATIVO') {
          this.router.navigate(['/painel-usuario']).then(() => {
            window.location.reload();
          });
        } else {
          this.errorMessage = 'Tipo de usuário desconhecido ou status inválido.';
        }
      },
      error: error => {
        this.errorMessage = 'Credenciais inválidas.';
        console.error('Erro no login:', error);
      }
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}