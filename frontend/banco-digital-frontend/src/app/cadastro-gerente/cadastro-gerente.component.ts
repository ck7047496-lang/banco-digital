import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UsuarioService } from '../usuario.service';
import { Usuario } from '../models/usuario.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cadastro-gerente',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-gerente.component.html',
  styleUrls: ['./cadastro-gerente.component.css']
})
export class CadastroGerenteComponent implements OnInit {
  cadastroGerenteForm!: FormGroup;
  mensagem: string = '';

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService
  ) { }

  ngOnInit(): void {
    this.cadastroGerenteForm = this.fb.group({
      nome: ['', Validators.required],
      senha: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  cadastrarGerente() {
    if (this.cadastroGerenteForm.valid) {
      this.usuarioService.cadastrarGerente(this.cadastroGerenteForm.value).subscribe({
        next: (response) => {
          this.mensagem = 'Gerente cadastrado com sucesso!';
          this.cadastroGerenteForm.reset();
        },
        error: (error) => {
          this.mensagem = 'Erro ao cadastrar gerente: ' + (error.error?.message || error.message);
        }
      });
    }
  }
}