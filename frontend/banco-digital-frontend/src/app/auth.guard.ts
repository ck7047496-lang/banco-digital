import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    // Usando sessionStorage para maior segurança contra XSS em comparação com localStorage.
    // Para segurança máxima, considere HttpOnly cookies, mas isso requer mudanças no backend.
    const token = sessionStorage.getItem('authToken');

    if (token && token.trim() !== '') {
      try {
        const decodedToken: any = jwtDecode(token);
        const expectedRole = route.data['role'];
        const rolesFromToken = decodedToken.roles;
        const userRoles: string[] = Array.isArray(rolesFromToken) ? rolesFromToken : (rolesFromToken ? [rolesFromToken] : []);

        if (userRoles.includes(expectedRole)) {
          console.log("AuthGuard: Acesso permitido.");
          return true;
        } else {
          console.error("AuthGuard: Acesso negado. O usuário não possui o papel esperado.", { userRoles, expectedRole });
          this.router.navigate(['/login']);
          return false;
        }
      } catch (error) {
        console.error("AuthGuard: Erro ao decodificar token ou verificar acesso.", error);
        this.router.navigate(['/login']);
        return false;
      }
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}