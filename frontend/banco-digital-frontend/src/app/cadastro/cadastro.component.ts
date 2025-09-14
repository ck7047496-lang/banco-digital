import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CadastroPopupComponent } from '../cadastro-popup/cadastro-popup.component';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, CadastroPopupComponent],
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css']
})
export class CadastroComponent implements OnInit {
  showCadastroPopup: boolean = true; // Controla a visibilidade do pop-up

  constructor() { }

  ngOnInit(): void {
    // O pop-up de cadastro será exibido automaticamente ao acessar esta rota
  }

  closeCadastroPopup(): void {
    this.showCadastroPopup = false;
    // Opcional: redirecionar para a landing page ou outra página após fechar o pop-up
    // this.router.navigate(['/']);
  }
}