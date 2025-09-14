import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { UserDashboardComponent } from './painel-usuario/user-dashboard.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { CadastroComponent } from './cadastro/cadastro.component';
import { PainelGerenteComponent } from './painel-gerente/painel-gerente.component';
import { AguardandoAnaliseComponent } from './aguardando-analise/aguardando-analise.component';
import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'cadastro', component: CadastroComponent },
  { path: 'painel-usuario', component: UserDashboardComponent, canActivate: [AuthGuard], data: { role: 'ROLE_CLIENTE' } },
  { path: 'painel-gerente', component: PainelGerenteComponent, canActivate: [AuthGuard], data: { role: 'ROLE_GERENTE' } },
  { path: 'aguardando-analise', component: AguardandoAnaliseComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }