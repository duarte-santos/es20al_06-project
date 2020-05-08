describe('Change StudentQuestion Dashboard Privacy walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.gotoSQDashboard();
  });

  afterEach(() => {
    cy.logout();
  });

  it('[ok] changes sq dashboard privacy', () => {
    cy.get('[data-cy="privacy"]')
      .parent()
      .click();
  });
});
