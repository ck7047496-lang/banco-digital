import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { map } from 'rxjs/operators';
import { Usuario } from './models/usuario.model';
import { Emprestimo } from './models/emprestimo.model';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private baseUrl = 'http://localhost:8080'; // URL base para a API

  constructor(private http: HttpClient) {}

  cadastrarUsuario(usuario: Partial<Usuario>): Observable<Usuario> {
    const clienteCadastroDTO = {
      nome: usuario.nome,
      cpf: usuario.cpf,
      endereco: usuario.endereco,
      email: usuario.email,
      senha: usuario.senha
    };
    return this.http.post<Usuario>(`${this.baseUrl}/auth/register`, clienteCadastroDTO);
  }

  login(identifier: string, senha: string): Observable<any> {
    const credentials = { identifier, senha };
    return this.http.post<any>(`${this.baseUrl}/auth/login`, credentials).pipe(
      tap(response => {
        if (response.token) {
          sessionStorage.setItem('authToken', response.token);
          sessionStorage.setItem('userRole', response.papel);
          sessionStorage.setItem('cpf', response.cpf);
          sessionStorage.setItem('email', response.email);
          sessionStorage.setItem('nome', response.nome);
          sessionStorage.setItem('status', response.status);
        }
      })
    );
  }

  // getUsuarioDados() não é mais necessário, pois o login já retorna os dados completos do usuário.
  // Se ainda for necessário buscar dados do usuário em outros momentos, o endpoint /api/usuario/dados
  // deve ser ajustado no backend para retornar o usuário logado.

  // --- Métodos para o Admin (antigo Gerente) ---

  getTodosUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.baseUrl}/api/admin/usuarios`);
  }

  aprovarUsuario(id: number): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.baseUrl}/api/admin/usuarios/${id}/aprovar`, {});
  }

  reprovarUsuario(id: number): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.baseUrl}/api/admin/usuarios/${id}/recusar`, {});
  }

  getEmprestimosPendentes(): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(`${this.baseUrl}/api/admin/emprestimos?status=SOLICITADO`);
  }

  aprovarEmprestimo(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/api/admin/emprestimos/${id}/aprovar`, {});
  }

  // --- Métodos para o Usuário (antigo Cliente) ---

  solicitarEmprestimo(emprestimo: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/cliente/solicitar-emprestimo`, emprestimo);
  }

  getMeusEmprestimos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/cliente/emprestimos`);
  }

  getDadosUsuarioAutenticado(): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.baseUrl}/api/cliente/dados`);
  }

  getSaldo(): Observable<number> {
    return this.http.get<Usuario>(`${this.baseUrl}/api/cliente/dados`).pipe(
      tap(response => {
        sessionStorage.setItem('saldo', response.saldo.toString());
      }),
      map(response => response.saldo)
    );
  }

  cadastrarGerente(gerente: Partial<Usuario>): Observable<Usuario> {
    // O backend espera um RegisterRequest com o papel definido como ROLE_GERENTE
    const gerenteCadastroDTO = {
      nome: gerente.nome,
      cpf: gerente.cpf,
      endereco: gerente.endereco,
      email: gerente.email,
      senha: gerente.senha,
      papel: 'ROLE_GERENTE' // Definir o papel explicitamente
    };
    return this.http.post<Usuario>(`${this.baseUrl}/auth/register/gerente`, gerenteCadastroDTO);
  }
  logout(): void {
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('userRole');
    sessionStorage.removeItem('cpf');
    sessionStorage.removeItem('email');
    sessionStorage.removeItem('nome');
    sessionStorage.removeItem('status');
    sessionStorage.removeItem('papel');
  }

  getToken(): string | null {
    return sessionStorage.getItem('authToken');
  }

  getRole(): string | null {
    return sessionStorage.getItem('userRole');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getUsernameFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.sub; // 'sub' geralmente armazena o username
    }
    return null;
  }

  getCpf(): string | null {
    return sessionStorage.getItem('cpf');
  }

  getEmail(): string | null {
    return sessionStorage.getItem('email');
  }

  getNome(): string | null {
    return sessionStorage.getItem('nome');
  }

  getStatus(): string | null {
    return sessionStorage.getItem('status');
  }

  getPapel(): string | null {
    return sessionStorage.getItem('papel');
  }
  getContagemUsuarios(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/api/admin/usuarios/count`);
  }

  trocarSenhaGerente(emailGerente: string, novaSenha: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/api/admin/gerente/trocar-senha`, { email: emailGerente, novaSenha });
  }
}

