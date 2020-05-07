describe('Enroll in a Tournament Walkthrough', () => {

  const ID = 666
  const TITLE = 'TorneioTeste'
  const CREATOR = 651

  beforeEach(() => {
    cy.demoStudentLogin()
    // Make sure the tournament is not already in the database
    cy.deleteTournament(ID)
  })

  afterEach(() => {
     cy.contains('Demo Course').click()
     cy.contains('Logout').click()
     cy.deleteTournament(ID)
  })

  it('login, enroll in an open tournament, check if the quiz was created', () => {

    cy.createTournamentDifferentStudent(ID, TITLE, CREATOR)

    cy.log("Enroll in the tournament")
    cy.goToTournamentEnrollments()
    cy.enrollInTournament(TITLE)

    cy.log("Check if START button appears = Quiz was generated")
    cy.checkTournamentQuiz(TITLE)

  });

});
