import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-aguardando-analise',
  standalone: true,
  templateUrl: './aguardando-analise.component.html',
  styleUrls: ['./aguardando-analise.component.css']
})
export class AguardandoAnaliseComponent {

  constructor(private router: Router) { }

  voltarParaInicio(): void {
    this.router.navigate(['/']);
  }
}
