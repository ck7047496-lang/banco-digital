import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent {
  currentYear: number = new Date().getFullYear(); // ✅ Ano atual

  constructor(private router: Router) {}

  accessAccount() {
    this.router.navigate(['/login']);
  }

  createAccount() {
    this.router.navigate(['/cadastro']);
  }
}
