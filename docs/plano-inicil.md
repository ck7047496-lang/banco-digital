Plano de Desenvolvimento do Sistema Web Bancário
1. Objetivo
Desenvolver um sistema web bancário com tema escuro, bordas arredondadas, sombras laterais e efeitos de luz roxa/azul, utilizando HTML, CSS, JavaScript (Vanilla) e Tailwind CSS para o frontend, integrado a um backend em Java com Spring Boot, Oracle e RabbitMQ. O sistema deve simular um banco digital com cadastro de cliente, aprovação de cadastro pelo gerente, e plataforma do cliente para visualização de saldo e solicitação de empréstimo.

2. Estrutura Geral do Projeto
O projeto será dividido em dois repositórios principais:

Frontend: Interface web com HTML, CSS, JavaScript (Vanilla) e Tailwind CSS.
Backend: API RESTful com Java 17, Spring Boot, Spring Security, Spring Data JPA, Hibernate, Lombok, RabbitMQ e Oracle.

2.1. Estrutura de Diretórios
Frontend
frontend/
├── index.html                # Página inicial (login)
├── cadastro.html            # Tela de cadastro de cliente
├── gerente.html             # Painel do gerente
├── cliente.html             # Plataforma do cliente
├── assets/
│   ├── css/
│   │   └── styles.css      # Estilos globais com Tailwind CSS
│   ├── js/
│   │   ├── auth.js         # Lógica de autenticação
│   │   ├── cadastro.js     # Lógica do cadastro
│   │   ├── gerente.js      # Lógica do painel do gerente
│   │   ├── cliente.js      # Lógica da plataforma do cliente
│   └── img/
│       └── background.jpg  # Imagem de fundo para o tema escuro
├── README.md                # Instruções para rodar o frontend

Backend
backend/
├── src/main/java/
│   ├── config/             # Configurações (Spring Security, RabbitMQ)
│   ├── controller/         # Controladores REST
│   ├── service/            # Lógica de negócio
│   ├── repository/         # Repositórios JPA
│   ├── model/              # Entidades JPA
│   ├── dto/                # Objetos de transferência de dados
│   └── exception/          # Tratamento de erros
├── src/main/resources/
│   ├── application.yml     # Configurações do Spring (Oracle, RabbitMQ)
│   └── sql/
│       └── schema.sql      # Scripts SQL para Oracle
├── pom.xml                 # Dependências Maven
├── README.md               # Instruções para rodar o backend


3. Requisitos e Funcionalidades
3.1. Frontend (HTML + CSS + JavaScript + Tailwind CSS)

Tecnologias: HTML5, CSS3, JavaScript (Vanilla), Tailwind CSS (via CDN).
Páginas:
Login: Formulário para autenticação de cliente e gerente, com validação de campos e feedback visual.
Cadastro de Cliente: Formulário para cadastro com campos como nome, CPF, e-mail, senha, telefone, etc.
Painel do Gerente: Lista de cadastros pendentes com botões para aprovar/reprovar.
Plataforma do Cliente: Exibição do saldo, formulário para solicitação de empréstimo com simulação de parcelas.


Design:
Tema escuro com fundo gradiente (roxos e azuis) e imagem de fundo sutil.
Bordas arredondadas em botões, cartões e formulários.
Sombras laterais para profundidade visual.
Efeitos de luz (gradientes roxo/azul) em botões e elementos interativos.
Layout responsivo com Tailwind CSS, priorizando profissionalidade bancária.


Interatividade:
Validações de formulário em JavaScript (ex.: CPF válido, e-mail único).
Consumo de APIs REST do backend via fetch.
Feedback visual para ações (ex.: loading, erro, sucesso).
Navegação entre páginas sem recarregar (usando JavaScript para manipulação do DOM).



3.2. Backend (Java + Spring Boot)

Tecnologias: Java 17, Spring Boot, Spring Security, Spring Data JPA, Hibernate, Lombok, RabbitMQ, Oracle.
Endpoints REST:
Autenticação: /api/auth/login (POST) – Gera token JWT para cliente/gerente.
Cadastro: /api/clientes/cadastro (POST) – Cria cliente e envia mensagem via RabbitMQ.
Painel do Gerente: 
/api/gerente/cadastros (GET) – Lista cadastros pendentes.
/api/gerente/cadastros/{id}/aprovar (PATCH) – Aprova cadastro.
/api/gerente/cadastros/{id}/reprovar (PATCH) – Reprova cadastro.


Plataforma do Cliente:
/api/clientes/{id}/saldo (GET) – Retorna saldo.
/api/clientes/{id}/emprestimo (POST) – Solicita empréstimo.
/api/clientes/{id}/emprestimo (GET) – Verifica empréstimo ativo.




Segurança:
Spring Security com JWT para autenticação.
Roles: ROLE_CLIENTE (acesso à plataforma do cliente) e ROLE_GERENTE (acesso ao painel do gerente).
Senhas criptografadas com BCrypt.


Integração Assíncrona:
RabbitMQ para enviar mensagem do cadastro ao gerente.
Fila: cadastro-pendente para notificações.


Persistência:
Entidades JPA: Cliente, Emprestimo, Usuario (para autenticação).
Banco Oracle com tabelas para clientes, empréstimos e usuários.




4. Plano de Desenvolvimento
4.1. Fase 1: Configuração do Ambiente

Duração: 1 dia
Atividades:
Configurar ambiente local: Java 17, Maven, Oracle Database, RabbitMQ.
Criar repositórios Git para frontend e backend.
Configurar projeto Spring Boot com dependências (Spring Web, Security, Data JPA, Lombok, RabbitMQ).
Configurar Tailwind CSS via CDN no frontend.
Criar scripts SQL iniciais para Oracle (tabelas de clientes, usuários, empréstimos).



4.2. Fase 2: Desenvolvimento do Backend

Duração: 3-4 dias
Atividades:
Modelagem de Dados:
Criar entidades JPA: Cliente (nome, CPF, e-mail, telefone, status), Emprestimo (valor, parcelas, juros, status), Usuario (login, senha, role).
Configurar relacionamentos e validações com Hibernate.


Configuração de Segurança:
Implementar Spring Security com JWT.
Definir roles (ROLE_CLIENTE, ROLE_GERENTE).
Configurar endpoints protegidos.


Endpoints REST:
Implementar endpoints para autenticação, cadastro, aprovação e plataforma do cliente.
Criar DTOs para entrada/saída de dados.


Integração com RabbitMQ:
Configurar producer para enviar mensagens de cadastro.
Configurar consumer para processar aprovações no painel do gerente.


Testes:
Criar testes unitários para serviços (JUnit).
Criar testes de integração para endpoints (Spring Test).





4.3. Fase 3: Desenvolvimento do Frontend

Duração: 3-4 dias
Atividades:
Estrutura HTML:
Criar páginas: index.html (login), cadastro.html, gerente.html, cliente.html.
Usar Tailwind CSS para layout responsivo e tema escuro.


Estilização CSS:
Aplicar tema escuro com gradientes roxo/azul.
Adicionar bordas arredondadas e sombras laterais.
Implementar efeitos de luz (hover em botões, gradientes).
Usar imagem de fundo sutil com opacidade.


JavaScript:
Implementar validações de formulário (ex.: CPF, e-mail).
Consumir APIs REST com fetch.
Gerenciar navegação entre páginas via JavaScript.
Adicionar feedback visual (ex.: loading, mensagens de erro/sucesso).


Testes:
Testar interatividade manualmente no navegador.
(Opcional) Criar testes unitários com Jest para funções JavaScript.





4.4. Fase 4: Integração e Testes

Duração: 2 dias
Atividades:
Integrar frontend com backend via chamadas REST.
Testar fluxo completo: cadastro → mensagem RabbitMQ → aprovação → acesso do cliente.
Validar segurança (autenticação, autorização).
Testar responsividade do frontend em diferentes dispositivos.



4.5. Fase 5: Documentação e Entrega

Duração: 1 dia
Atividades:
Criar README.md com:
Instruções para rodar o backend (Maven, Oracle, RabbitMQ).
Instruções para rodar o frontend (servidor estático, ex.: Live Server).
Documentação dos endpoints (tabela com métodos, URLs, payloads).
Usuários de exemplo (cliente: login/senha, gerente: login/senha).
Scripts SQL para Oracle.


(Opcional) Configurar Swagger para documentação da API.
Empacotar projeto em ZIP ou disponibilizar no GitHub.




5. Design Visual

Tema Escuro:
Fundo: Gradiente de #1a1a2e (azul escuro) para #16213e (roxo escuro).
Imagem de fundo: Textura sutil com opacidade 0.2.


Bordas Arredondadas:
border-radius: 12px em cartões, botões e formulários.


Sombras:
box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3) para profundidade.
Sombras laterais com box-shadow: 5px 0 15px rgba(108, 99, 255, 0.2) (roxo claro).


Efeitos de Luz:
Botões com background: linear-gradient(45deg, #6c63ff, #00ddeb) (roxo para azul).
Hover com transição suave e brilho (filter: brightness(1.2)).


Tipografia:
Fontes sans-serif modernas (ex.: Inter, Roboto via Tailwind).
Tamanhos: text-xl para títulos, text-base para textos.




6. Diferenciais

Testes Automatizados:
Backend: Testes unitários (JUnit) para serviços e integração (Spring Test).
Frontend: Testes unitários com Jest para validações JavaScript.


Swagger:
Configurar Springdoc OpenAPI para documentação interativa dos endpoints.


Docker:
(Opcional) Criar Dockerfile para Oracle e RabbitMQ, facilitando execução local.




7. Cronograma

Total Estimado: 10-12 dias
Fase 1: 1 dia
Fase 2: 3-4 dias
Fase 3: 3-4 dias
Fase 4: 2 dias
Fase 5: 1 dia




8. Próximos Passos

Aguardar respostas às perguntas enviadas anteriormente para ajustar o plano, se necessário.
Iniciar configuração do ambiente e esboço do modelo de dados.
Desenvolver protótipo inicial do frontend (HTML + Tailwind CSS) para validação do design.
Criar endpoints REST iniciais para autenticação e cadastro.
