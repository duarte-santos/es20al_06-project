describe('Checking Tournament results in dashboard Walkthrough', () => {

  const ID = 666
  const TITLE = 'TorneioTeste'
  const CREATOR = 651

  beforeEach(() => {
    cy.demoStudentLogin()

    // Make sure the tournament is not already in the database
    cy.log("SQL command (requires db access credentials)")
    cy.deleteTournament(ID)
  })

  afterEach(() => {
    cy.contains('Demo Course').click()
    cy.contains('Logout').click()
    cy.log("SQL command (requires db access credentials)")
    cy.deleteTournament(ID)
  })

  it('login, answer a quiz and check the results in dashboard', () => {

    cy.log("SQL command (requires db access credentials)")
    cy.createTournamentDifferentStudent(ID, TITLE, CREATOR)

    cy.log("Enroll in the tournament")
    cy.goToTournamentEnrollments()
    cy.enrollInTournament(TITLE)

    cy.wait(1000)
    cy.answerTournamentQuiz(TITLE)

    cy.log("Close tournament (requires db access credentials)")
    cy.closeTournament(ID)

    cy.goToDashboard()
    cy.wait(1000)
    cy.checkTournamentInDashboard(TITLE)

  });

  it('change dashboard privacy', () => {
    cy.goToDashboard()
    cy.wait(1000)
    // Change privacy
    cy.get('[data-cy="privacy"]')
      .parent()
      .click();
  });



});
