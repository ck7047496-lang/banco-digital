describe('Fluxo de Cadastro de Usuário', () => {
  beforeEach(() => {
    cy.wait(5000); // Espera 5 segundos para o aplicativo carregar
    cy.visit('/cadastro'); // Assumindo que a rota de cadastro é /cadastro
  });

  it('deve registrar um novo usuário com sucesso', () => {
    cy.get('input[id="cpf"]').should('be.visible').type('11122233344');
    cy.get('button').contains('Próximo').click();
    cy.get('input[id="nome"]').should('be.visible').type('Novo Usuário Teste');
    cy.get('input[id="email"]').should('be.visible').type('novo.usuario@teste.com');
    cy.get('input[id="endereco"]').should('be.visible').type('Rua Teste, 123');
    cy.get('input[id="telefone"]').should('be.visible').type('11987654321');
    cy.get('button').contains('Próximo').click();
    cy.get('input[name="senha"]').should('be.visible').type('senha123');
    cy.get('input[id="confirmarSenha"]').should('be.visible').type('senha123');
    cy.get('button').contains('Cadastrar').click();

    // Verificar se o usuário é redirecionado para a tela de espera ou painel
    cy.url().should('include', '/aguardando-analise'); // Ou a rota esperada após o cadastro
    cy.contains('Aguardando Análise').should('be.visible');
  });

  it('deve exibir mensagem de erro se as senhas não coincidirem', () => {
    cy.get('input[id="cpf"]').should('be.visible').type('55566677788');
    cy.get('button').contains('Próximo').click();
    cy.get('input[id="nome"]').should('be.visible').type('Outro Usuário');
    cy.get('input[id="email"]').should('be.visible').type('outro.usuario@teste.com');
    cy.get('input[id="endereco"]').should('be.visible').type('Av. Teste, 456');
    cy.get('input[id="telefone"]').should('be.visible').type('11912345678');
    cy.get('button').contains('Próximo').click();
    cy.get('input[name="senha"]').should('be.visible').type('senha123');
    cy.get('input[id="confirmarSenha"]').should('be.visible').type('senhaDiferente');
    cy.get('button').contains('Cadastrar').click();

    cy.contains('As senhas não coincidem.').should('be.visible');
    cy.url().should('include', '/cadastro'); // Deve permanecer na página de cadastro
  });

  // Adicionar mais testes para outros cenários de validação, como campos vazios, CPF inválido, etc.
});