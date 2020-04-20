describe('Enroll in a Tournament', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.gotoCreateTournamentPage()
  })

  afterEach(() => {
     cy.contains('Logout').click()
  })

  it('Enroll in a tournament', () => {
    let name = 'Subaru4';
    cy.createClosedTournament(name, 25, true, true, true)
    cy.goToTournamentEnrollments()
    cy.tryToEnrollInTournament(name)
    //cy.shouldCloseConfirmationAlert()
  });

  it('Try to enroll in a tournament while already enrolled', () => {
    let name = 'Subaru7';
    cy.createClosedTournament(name, 25, true, true, true)
    cy.goToTournamentEnrollments()
    cy.tryToEnrollInTournament(name)
    //cy.shouldCloseConfirmationAlert()
    cy.tryToEnrollInTournament(name)
    cy.closeErrorMessage()

  });

});
