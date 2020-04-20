describe('Checking Open Tournaments', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.gotoCreateTournamentPage()
  })

  afterEach(() => {
    cy.contains('Logout').click()
    //should delete after each
  })

  it('logs in, creates open tournament, and sees it', () => {
    cy.createOpenTournament('Torneio3',10 ,true, true, true)
    cy.checkForTournament('Torneio3')
    cy.contains('Demo Course').click()

  });

  it('logs in, creates closed tournament, and can not see it', () => {
    cy.createClosedTournament('Torneio2',10 ,true, true, true)
    cy.checkForNoTournament('Torneio2')
    cy.contains('Demo Course').click()

  });


});
