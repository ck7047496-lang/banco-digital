-- Exclui as tabelas existentes para garantir uma recriação limpa
DROP TABLE IF EXISTS emprestimos;
DROP TABLE IF EXISTS usuarios;

-- ====================================
-- CRIAÇÃO DA TABELA USUÁRIOS
-- ====================================
-- Tabela unificada para todos os usuários do sistema (clientes e gerentes)
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(255) UNIQUE,
    endereco VARCHAR(255),
    senha VARCHAR(255) NOT NULL,
    -- Define o papel do usuário para controle de permissões
    papel VARCHAR(50) NOT NULL CHECK (papel IN ('ROLE_CLIENTE', 'ROLE_GERENTE')),
    -- Status geral da conta do usuário
    status VARCHAR(30) NOT NULL DEFAULT 'ATIVO',
    -- Limite de crédito pré-aprovado pelo gerente
    limite_credito NUMERIC(19, 2) DEFAULT 0.00,
    -- Situação específica da análise de crédito
    situacao_credito VARCHAR(30) NOT NULL DEFAULT 'PENDENTE' CHECK (situacao_credito IN ('PENDENTE', 'APROVADO', 'NEGADO')),
    saldo NUMERIC(19, 2) DEFAULT 0.00
);

-- ====================================
-- CRIAÇÃO DA TABELA EMPRÉSTIMOS
-- ====================================
-- Armazena o histórico de todas as solicitações de empréstimo
CREATE TABLE emprestimos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    valor NUMERIC(12,2) NOT NULL,
    parcelas INTEGER NOT NULL,
    valor_total NUMERIC(12,2) NOT NULL,
    valor_parcela NUMERIC(12,2) NOT NULL,
    juros NUMERIC(7,4) NOT NULL,
    data_solicitacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Status da solicitação de empréstimo
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE' CHECK (status IN ('PENDENTE', 'APROVADO', 'NEGADO')),
    -- Chave estrangeira que liga o empréstimo ao usuário que o solicitou
    usuario_id INTEGER NOT NULL,
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

-- ====================================
-- ÍNDICES PARA OTIMIZAÇÃO DE CONSULTAS
-- ====================================
CREATE INDEX IF NOT EXISTS idx_usuarios_cpf ON usuarios (cpf);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios (email);
CREATE INDEX IF NOT EXISTS idx_emprestimos_usuario ON emprestimos (usuario_id);
CREATE INDEX IF NOT EXISTS idx_emprestimos_status ON emprestimos (status);

-- ====================================
-- INSERIR USUÁRIO GERENTE PADRÃO
-- ====================================
-- Insere o gerente com crédito já aprovado para que ele possa operar no sistema
INSERT INTO usuarios (nome, senha, papel, status, saldo, limite_credito, situacao_credito, email)
VALUES (
  'gerente',
  '$2a$10$BhuFGqssI6MWkUWDtMirCO0fIpcCIvQGW1C8oIu7JNAUgjruFB3NC', -- Senha '687813' criptografada com BCrypt
  'ROLE_GERENTE',
  'ATIVO',
  0.00,
  0.00,
  'APROVADO',
  'gerente@banco.com' -- Adicionado um e-mail para o gerente
);