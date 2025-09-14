import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Emprestimo, EmprestimoRequest } from '../models/emprestimo.model';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class EmprestimoService {
  private baseUrl = 'http://localhost:8080';
  private stompClient!: Client;
  private emprestimosPendentesSubject: BehaviorSubject<Emprestimo[]> = new BehaviorSubject<Emprestimo[]>([]);
  public emprestimosPendentes$: Observable<Emprestimo[]> = this.emprestimosPendentesSubject.asObservable();

  private emprestimosAtualizacoesSubject: BehaviorSubject<Emprestimo[]> = new BehaviorSubject<Emprestimo[]>([]);
  public emprestimosAtualizacoes$: Observable<Emprestimo[]> = this.emprestimosAtualizacoesSubject.asObservable();


  constructor(private http: HttpClient) {
    this.initStompClient();
  }

  private initStompClient() {
    this.stompClient = new Client({
      webSocketFactory: () => new (SockJS as any)(`${this.baseUrl}/ws`),
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Connected: ' + frame);
      this.stompClient.subscribe('/topic/emprestimos/pendentes', (message) => {
        const emprestimos = JSON.parse(message.body) as Emprestimo[];
        this.emprestimosPendentesSubject.next(emprestimos);
      });

      this.stompClient.subscribe('/topic/emprestimos/atualizacoes', (message) => {
        const emprestimos = JSON.parse(message.body) as Emprestimo[];
        this.emprestimosAtualizacoesSubject.next(emprestimos);
      });
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    this.stompClient.activate();
  }

  getMeusEmprestimos(): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(`${this.baseUrl}/api/usuario/emprestimos`);
  }

  solicitarEmprestimo(emprestimo: EmprestimoRequest): Observable<Emprestimo> {
    return this.http.post<Emprestimo>(`${this.baseUrl}/api/cliente/solicitar-emprestimo`, emprestimo);
  }

  aprovarEmprestimo(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/api/admin/emprestimos/${id}/aprovar`, {});
  }

  reprovarEmprestimo(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/api/admin/emprestimos/${id}/reprovar`, {});
  }

  getEmprestimosPendentesGerente(): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(`${this.baseUrl}/api/admin/emprestimos?status=PENDENTE`);
  }

  getEmprestimosCountByStatus(status: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/api/admin/emprestimos/count?status=${status}`);
  }

  listarTodosEmprestimos(): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(`${this.baseUrl}/api/admin/emprestimos`);
  }
}