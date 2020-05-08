describe('Cancel a Tournament Walkthrough', () => {

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
    // Canceling the quizz deletes it, so there is no need to clean the db ate the end
  })

  it('login, cancel a tournament', () => {
    cy.gotoCreateTournamentPage()
    cy.createClosedTournament(TITLE, 25, true, true, true)


    cy.log("Cancel the tournament")
    cy.goToTournamentCanceling()
    cy.cancelTheTournament(TITLE)

    cy.assertCanceled(TITLE)
  });



});
