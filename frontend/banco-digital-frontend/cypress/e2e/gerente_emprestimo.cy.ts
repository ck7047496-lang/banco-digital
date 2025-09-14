describe('Fluxo de Aprovação/Reprovação de Empréstimo pelo Gerente', () => {
  beforeEach(() => {
    // Assumindo que o gerente já está logado ou que o login é parte do setup
    cy.wait(5000); // Espera 5 segundos para o aplicativo carregar
    cy.visit('/login');
    cy.get('input[name="email"]').should('be.visible').type('gerente@banco.com'); // Credenciais do gerente
    cy.get('input[name="senha"]').should('be.visible').type('senha123');
    cy.get('button[type="submit"]').should('be.visible').click();
    cy.url().should('include', '/painel-gerente');
  });

  it('deve aprovar um empréstimo com sucesso', () => {
    // Assumindo que existe um empréstimo pendente para aprovação
    // Pode ser necessário criar um empréstimo via API antes do teste, ou ter um mock
    cy.contains('Empréstimos Pendentes').should('be.visible');
    cy.get('.emprestimo-item').first().find('button.aprovar').should('be.visible').click(); // Clicar no botão de aprovar do primeiro empréstimo

    cy.contains('Empréstimo aprovado com sucesso!').should('be.visible'); // Mensagem de sucesso
    cy.get('.emprestimo-item').should('not.contain', 'SOLICITADO'); // Verificar se o empréstimo não está mais como solicitado
  });

  it('deve reprovar um empréstimo com sucesso', () => {
    // Assumindo que existe outro empréstimo pendente para reprovação
    cy.contains('Empréstimos Pendentes').should('be.visible');
    cy.get('.emprestimo-item').first().find('button.reprovar').should('be.visible').click(); // Clicar no botão de reprovar do primeiro empréstimo

    cy.contains('Empréstimo reprovado com sucesso!').should('be.visible'); // Mensagem de sucesso
    cy.get('.emprestimo-item').should('not.contain', 'SOLICITADO'); // Verificar se o empréstimo não está mais como solicitado
  });

  // Adicionar mais testes para cenários como:
  // - Não há empréstimos pendentes
  // - Tentativa de aprovar/reprovar um empréstimo já processado
});