describe('Dashboard StudentQuestion walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.gotoSQDashboard();
  });

  afterEach(() => {
    cy.logout();
  });

  it('[ok] check student question dashboard', () => {
    cy.contains('Total Questions Submitted')
      .should('be.visible')
      .click();
    cy.contains('Total Approved Questions')
      .should('be.visible')
      .click();
    // the above 'click' is to prevent a bug associated with 'contains'
  });
});
