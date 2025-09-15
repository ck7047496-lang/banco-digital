# Banco Digital - Projeto de Migração para SQLite

Este projeto de banco digital foi desenvolvido por Clayton, com o objetivo de criar uma plataforma robusta e funcional para gerenciamento de contas e empréstimos. O sistema é composto por um frontend em Angular e um backend em Java (Spring Boot), utilizando SQLite como banco de dados local.

## Tecnologias Utilizadas

### Frontend (Angular)
*   **Angular:** Framework para construção de interfaces de usuário dinâmicas e reativas.
*   **TypeScript:** Superset do JavaScript que adiciona tipagem estática, melhorando a manutenibilidade e escalabilidade do código.
*   **HTML5/CSS3:** Estruturação e estilização das páginas web.
*   **RxJS:** Biblioteca para programação reativa, utilizada para lidar com eventos assíncronos e fluxos de dados.
*   **JWT (JSON Web Tokens):** Utilizado para autenticação e autorização de usuários.
*   **WebSockets:** Para comunicação em tempo real, garantindo a atualização instantânea do saldo do usuário após a aprovação de empréstimos.

### Backend (Java - Spring Boot)
*   **Java 17:** Linguagem de programação principal.
*   **Spring Boot:** Framework para desenvolvimento rápido de aplicações Java, com foco em microserviços.
*   **Spring Security:** Para segurança da aplicação, incluindo autenticação baseada em JWT e controle de acesso baseado em roles (usuário e gerente).
*   **Spring Data JPA:** Para persistência de dados, facilitando a interação com o banco de dados.
*   **SQLite:** Banco de dados relacional leve e embarcado, ideal para desenvolvimento e testes locais.
*   **Flyway:** Ferramenta de migração de banco de dados, garantindo que o esquema do banco esteja sempre atualizado.
*   **RabbitMQ:** Message broker utilizado para comunicação assíncrona entre os serviços, especialmente para o processamento de aprovação/rejeição de empréstimos e atualização de saldo via WebSockets.
*   **Jackson:** Biblioteca para serialização e desserialização de objetos Java para JSON, essencial para a comunicação com o RabbitMQ.
*   **Lombok:** Biblioteca que reduz o código boilerplate em classes Java.

## 1. Configuração do Banco de Dados (SQLite)

O banco de dados SQLite (`database.db`) será criado automaticamente na raiz do diretório `backend/banco-digital` quando a aplicação Spring Boot for iniciada pela primeira vez. Não é necessário configurar um servidor de banco de dados externo.

### Variáveis de Ambiente do Banco de Dados:

As configurações para a conexão com o SQLite estão definidas no arquivo `backend/banco-digital/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlite:database.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

## 2. Como Rodar o Backend (Spring Boot)

### Pré-requisitos:
- Java 17 ou superior.
- Maven.
- RabbitMQ instalado e em execução localmente.

### Comandos para rodar o Backend:

1.  **Navegue até o diretório do backend:**
    ```bash
    cd backend/banco-digital
    ```2.  **Compile e execute a aplicação Spring Boot:**
    ```bash
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```
    A aplicação estará disponível em `http://localhost:8080`. O arquivo `database.db` será criado e as migrações do Flyway serão aplicadas automaticamente.

## 3. Como Rodar o Frontend (Angular)

### Pré-requisitos:
- Node.js (versão LTS recomendada).
- npm ou yarn.
- Angular CLI (`npm install -g @angular/cli`).

### Comandos para rodar o Frontend:

1.  **Navegue até o diretório do frontend:**
    ```bash
    cd frontend/banco-digital-frontend
    ```
2.  **Instale as dependências:**
    ```bash
    npm install
    # ou yarn install
    ```
3.  **Inicie a aplicação Angular:**
    ```bash
    ng serve --open
    ```
    A aplicação será aberta automaticamente no seu navegador em `http://localhost:4200`.

## 4. Cadastro de Usuários

Após o cadastro inicial de um novo usuário, é necessário que um gerente aprove o cadastro para que o usuário possa acessar todas as funcionalidades do sistema, como solicitar empréstimos. O status inicial de um usuário recém-cadastrado é "PENDENTE".

## 5. Roteiro de Teste Detalhado

Para validar o fluxo completo de empréstimo e as funcionalidades do sistema, siga os passos abaixo. É crucial observar o console do navegador (F12) e a aba "Network" (Rede) para verificar as requisições e a comunicação WebSocket.

**Pré-requisitos:**

*   Certifique-se de que o backend (Spring Boot) esteja em execução.
*   Certifique-se de que o frontend (Angular) esteja em execução.
*   Certifique-se de que o RabbitMQ esteja em execução.

**Passos para Teste:**

1.  **Cadastro de Novo Usuário:**
    *   Acesse o frontend no seu navegador (geralmente `http://localhost:4200`).
    *   Clique em "Cadastrar" e preencha os dados para criar um novo usuário.
    *   Após o cadastro, o usuário estará com o status "PENDENTE" e não poderá realizar operações financeiras.
2.  **Login como Gerente e Aprovação de Usuário:**
    *   Faça login com as credenciais de um gerente.
    *   Acesse o painel de gerenciamento de usuários.
    *   Localize o novo usuário com status "PENDENTE" e aprove o cadastro.
3.  **Login como Usuário Aprovado:**
    *   Faça logout do gerente.
    *   Faça login com o usuário recém-aprovado.
    *   Verifique se o saldo é exibido corretamente no painel do usuário.
4.  **Solicitar Empréstimo:**
    *   No painel do usuário, clique em "Solicitar Empréstimo".
    *   Preencha o valor e o número de parcelas.
    *   Verifique se a simulação do empréstimo é exibida corretamente.
    *   Clique em "Solicitar Empréstimo" e confirme a solicitação no pop-up.
    *   Verifique se a mensagem de sucesso "Solicitação de empréstimo enviada com sucesso! Aguardando aprovação." é exibida.
    *   No histórico de empréstimos, o novo empréstimo deve aparecer com o status "PENDENTE".
5.  **Login como Gerente (para aprovar empréstimo):**
    *   Faça logout do usuário.
    *   Faça login novamente com as credenciais de um gerente.
    *   Acesse o painel de "Gerenciar Empréstimos".
    *   Verifique se o empréstimo solicitado pelo usuário aparece na lista com o status "PENDENTE".
    *   Verifique se os ícones `users.svg` e `logout.svg` estão sendo exibidos corretamente no painel do gerente.
6.  **Aprovar Empréstimo (Gerente):**
    *   No painel do gerente, localize o empréstimo "PENDENTE" e clique na ação para aprová-lo.
    *   **Monitore o console do navegador (F12) e a aba "Network" (Rede)** para verificar se a requisição para `/api/admin/emprestimos/{id}/aprovar` retorna um status 200 OK e se não há erros no console.
    *   Após a aprovação, o status do empréstimo deve mudar para "APROVADO" no painel do gerente.
7.  **Verificar Atualização do Saldo (Usuário):**
    *   Faça logout do gerente.
    *   Faça login novamente com o usuário que solicitou o empréstimo.
    *   Verifique se o saldo do usuário foi atualizado para incluir o valor do empréstimo aprovado.
    *   Verifique se o status do empréstimo no histórico do usuário mudou para "APROVADO".
    *   **Monitore o console do navegador (F12) e a aba "Network" (Rede)** para verificar a comunicação WebSocket e a atualização do saldo.

Este roteiro cobre o fluxo completo de registro, aprovação de usuário, solicitação de empréstimo e aprovação de empréstimo, validando as funcionalidades implementadas com o backend Java e SQLite.