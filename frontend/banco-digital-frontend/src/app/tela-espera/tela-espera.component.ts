import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { UsuarioService } from '../usuario.service';
import { jwtDecode } from 'jwt-decode';
import { Subscription, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-tela-espera',
    standalone: true,
  templateUrl: './tela-espera.component.html',
  styleUrls: ['./tela-espera.component.css']
})
export class TelaEsperaComponent implements OnInit, OnDestroy {
  private statusSubscription!: Subscription;

  constructor(private usuarioService: UsuarioService, private router: Router) { }

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    const decodedToken: any = jwtDecode(token);
    const cpf = decodedToken.sub; // Assumindo que o 'sub' do token é o CPF

    this.statusSubscription = timer(0, 5000) // Verifica a cada 5 segundos
      .pipe(
        switchMap(() => this.usuarioService.getUsuarioDados())
      )
      .subscribe(response => {
        if (response && response.status === 'APROVADO') {
          this.router.navigate(['/painel-cliente']);
        }
      });
  }

  ngOnDestroy(): void {
    if (this.statusSubscription) {
      this.statusSubscription.unsubscribe();
    }
  }
}