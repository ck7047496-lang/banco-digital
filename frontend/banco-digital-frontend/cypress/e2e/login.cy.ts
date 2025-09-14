describe('Fluxo de Login de Usuário', () => {
  beforeEach(() => {
    cy.wait(5000); // Espera 5 segundos para o aplicativo carregar
    cy.visit('/login'); // Assumindo que a rota de login é /login
  });

  it('deve fazer login com sucesso com credenciais válidas', () => {
    // Assumindo que existe um usuário de teste pré-cadastrado no backend
    cy.get('input[name="email"]').should('be.visible').type('gerente@banco.com');
    cy.get('input[name="senha"]').should('be.visible').type('senha123');
    cy.get('button[type="submit"]').click();

    // Verificar se o usuário é redirecionado para o painel do gerente
    cy.url().should('include', '/painel-gerente');
    cy.contains('Bem-vindo, Gerente!').should('be.visible');
  });

  it('deve exibir mensagem de erro com credenciais inválidas', () => {
    cy.get('input[name="email"]').should('be.visible').type('usuario.invalido@teste.com');
    cy.get('input[name="senha"]').should('be.visible').type('senhaInvalida');
    cy.get('button[type="submit"]').click();

    cy.contains('Credenciais inválidas').should('be.visible'); // Ou a mensagem de erro esperada
    cy.url().should('include', '/login'); // Deve permanecer na página de login
  });

  // Adicionar mais testes para outros cenários de login, como campos vazios, etc.
});