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
    cy.log("Check if START button appears = Quiz was generated")
    cy.answerTournamentQuiz(TITLE)

    cy.goToDashboard()
    cy.wait(1000)



  });

});
