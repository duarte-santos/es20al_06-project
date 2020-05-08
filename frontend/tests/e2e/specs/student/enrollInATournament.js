describe('Enroll in a Tournament Walkthrough', () => {

  const ID = 666
  const TITLE = 'TorneioTeste'
  const CREATOR = 651

  beforeEach(() => {
    cy.demoStudentLogin()
    cy.wait(200)
    // Make sure the tournament is not already in the database
    cy.log("SQL command (requires db access credentials)")
    cy.deleteTournament(ID)
  })


  afterEach(() => {
    cy.contains('Demo Course').click()
    cy.contains('Logout').click()
    cy.wait(200)
    cy.log("SQL command (requires db access credentials)")
    cy.deleteTournament(ID)
  })

  it('login, enroll in a tournament', () => {

    cy.log("SQL command (requires db access credentials)")
    cy.createTournamentDifferentStudent(ID, TITLE, CREATOR)

    cy.log("Enroll in a tournament")
    cy.goToTournamentEnrollments()
    cy.enrollInTournament(TITLE)

    cy.assertEnrolled(TITLE)
  });

});
