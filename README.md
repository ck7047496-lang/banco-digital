# Banco Digital

Este projeto é uma aplicação de banco digital completa, dividida em um backend construído com Spring Boot e um frontend desenvolvido com Angular. Ele oferece funcionalidades para usuários (clientes) e gerentes, incluindo autenticação, gerenciamento de empréstimos, e comunicação em tempo real.


https://github.com/user-attachments/assets/242d97cb-bd31-4baa-ae8b-bb703d9d2ef4

## Tecnologias Utilizadas

### Backend (Spring Boot)
https://github.com/user-attachments/assets/25920a16-3023-473f-8dfe-3bf764e88e52
*   **Linguagem:** Java 17
*   **Framework:** Spring Boot 3.2.5
*   **Gerenciador de Dependências:** Maven
*   **Banco de Dados:** SQLite (configurado para `database.db`)
*   **ORM:** Spring Data JPA com Hibernate
*   **Segurança:** Spring Security e JWT (JSON Web Tokens) para autenticação e autorização
*   **Mensageria:** RabbitMQ para comunicação assíncrona
*   **Comunicação em Tempo Real:** Spring WebSocket
*   **Testes:** Spring Boot Starter Test, JUnit

### Frontend (Angular)
https://github.com/user-attachments/assets/304b14ea-3ec3-448c-9401-5fb842d42931
*   **Framework:** Angular 17
*   **Linguagem:** TypeScript
*   **Estilização:** Tailwind CSS
*   **Testes E2E:** Cypress
*   **Gráficos:** Chart.js, ng2-charts
*   **Geração de PDF:** jspdf, jspdf-autotable
*   **Decodificação JWT:** jwt-decode
*   **Reatividade:** RxJS
*   **WebSockets:** sockjs-client
*   **3D (potencial):** Three.js (presente nas dependências, mas o uso específico não foi detalhado nos arquivos analisados)
*   **Planilhas:** xlsx
*   **Autenticação/Backend as a Service:** Supabase (presente nas dependências, mas o uso específico não foi detalhado nos arquivos analisados)

## Arquitetura

O projeto segue uma arquitetura de microsserviços (ou uma aplicação monolítica bem modularizada) com uma clara separação entre o backend e o frontend.

*   **Backend:** Uma API RESTful desenvolvida com Spring Boot que lida com a lógica de negócios, persistência de dados (SQLite), segurança (JWT) e comunicação assíncrona (RabbitMQ) e em tempo real (WebSockets).
*   **Frontend:** Uma Single Page Application (SPA) construída com Angular que consome a API do backend. Ele oferece interfaces de usuário distintas para clientes e gerentes, com rotas protegidas por `AuthGuard`.
*   **Comunicação Assíncrona:** Utiliza RabbitMQ para processamento de tarefas em segundo plano ou comunicação entre serviços.
*   **Comunicação em Tempo Real:** WebSockets são empregados para funcionalidades que exigem atualizações instantâneas, como notificações ou atualizações de status.
*   **Autenticação:** Baseada em JWT, garantindo que apenas usuários autorizados acessem recursos específicos.

## Como Rodar o Projeto

### Pré-requisitos

*   Java Development Kit (JDK) 17 ou superior
*   Maven
*   Node.js e npm (ou yarn)
*   Git
*   Docker e Docker Compose (para o RabbitMQ, se usado via Docker)

### Configuração do Backend

1.  **Navegue até o diretório do backend:**
    ```bash
    cd backend/banco-digital
    ```
2.  **Compile o projeto Maven:**
    ```bash
    mvn clean install
    ```
3.  **Execute a aplicação Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```
    O backend estará disponível em `http://localhost:8080`.

### Configuração do Frontend

1.  **Navegue até o diretório do frontend:**
    ```bash
    cd frontend/banco-digital-frontend
    ```
2.  **Instale as dependências do Node.js:**
    ```bash
    npm install
    ```
3.  **Inicie a aplicação Angular:**
    ```bash
    npm start
    ```
    O frontend estará disponível em `http://localhost:4200` (ou outra porta, dependendo da configuração do Angular CLI).

### Executando Testes E2E (Cypress)

1.  Certifique-se de que o frontend esteja rodando (`npm start`).
2.  **Navegue até o diretório do frontend:**
    ```bash
    cd frontend/banco-digital-frontend
    ```
3.  **Abra o Cypress Test Runner:**
    ```bash
    npm run e2e
    ```
    Isso abrirá a interface do Cypress, onde você pode selecionar e executar os testes end-to-end.

## Estrutura de Pastas

*   `backend/banco-digital`: Contém o código-fonte do backend Spring Boot.
    *   `src/main/java/com/banco/bancodigital`: Classes Java da aplicação.
    *   `src/main/resources`: Arquivos de configuração (ex: `application.properties`) e scripts de migração de banco de dados.
    *   `src/test/java/com/banco/bancodigital`: Testes de integração e unitários do backend.
*   `frontend/banco-digital-frontend`: Contém o código-fonte da aplicação Angular.
    *   `src/app`: Componentes, serviços, módulos e rotas do Angular.
    *   `src/assets`: Imagens, ícones e outros recursos estáticos.
    *   `cypress`: Testes end-to-end com Cypress.
*   `docker-compose.yml`: Arquivo para orquestração de contêineres Docker (ex: RabbitMQ).
