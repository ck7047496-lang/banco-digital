import { Usuario } from './usuario.model'; // Importar a interface Usuario

export interface Emprestimo {
  id: number;
  valor: number;
  parcelas: number;
  valorTotal?: number; // Pode ser opcional se nem sempre vier preenchido
  valorParcela?: number; // Pode ser opcional se nem sempre vier preenchido
 dataSolicitacao: string;
 status: 'PENDENTE' | 'APROVADO' | 'NEGADO';
 usuario: Usuario;
}

export interface EmprestimoRequest {
 valor: number;
 parcelas: number;
}

export interface EmprestimoSolicitacaoDTO {
 valor: number;
 parcelas: number;
}