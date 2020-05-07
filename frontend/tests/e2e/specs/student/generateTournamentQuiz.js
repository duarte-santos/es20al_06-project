describe('Generating a quiz by enrolling in a Tournament Walkthrough', () => {

  const ID = 666
  const TITLE = 'TorneioTeste'
  const CREATOR = 651

  beforeEach(() => {
    cy.demoStudentLogin()

    // Make sure the tournament is not already in the database
    cy.log("SQL command (requires db acess credentials)")
    cy.wait(200)
    cy.deleteTournament(ID)
  })

  afterEach(() => {
    cy.contains('Demo Course').click()
    cy.contains('Logout').click()
    cy.log("SQL command (requires db acess credentials)")
    cy.wait(200)
    cy.deleteTournament(ID)
  })

  it('login, enroll in an open tournament, check if the quiz was created', () => {

    cy.log("SQL command (requires db acess credentials)")
    cy.createTournamentDifferentStudent(ID, TITLE, CREATOR)

    cy.log("Enroll in the tournament")
    cy.goToTournamentEnrollments()
    cy.enrollInTournament(TITLE)

    cy.log("Check if START button appears = Quiz was generated")
    cy.checkTournamentQuiz(TITLE)

  });

});
