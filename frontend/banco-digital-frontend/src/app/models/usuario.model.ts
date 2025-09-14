// Definição da interface para o objeto Usuario
// Isso ajuda a garantir a consistência dos dados em toda a aplicação.
export interface Usuario {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  telefone?: string;
  endereco: string;
  senha?: string;
  papel: 'ROLE_CLIENTE' | 'ROLE_GERENTE';
  status: string;
  limiteCredito: number;
  situacaoCredito: 'PENDENTE' | 'APROVADO' | 'NEGADO';
  saldo: number;
  token?: string;
}
