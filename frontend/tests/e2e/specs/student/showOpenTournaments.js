describe('Checking All Open Tournaments', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.gotoCreateTournamentPage()
  })

  const NAME1 = 'Torneio3';
  const NAME2 = 'Torneio4';

  afterEach(() => {
    cy.contains('Demo Course').click()
    cy.contains('Logout').click()
    //should delete after each, but we don't have a functionality for it
  })

  it('login, creates open tournament, and sees it', () => {
    cy.createOpenTournament(NAME1, 10, true, true, true)

    cy.log("Check if we can see the open tournament")
    cy.checkForTournament(NAME1)

  });

  it('login, creates closed tournament, and cant see it', () => {
    cy.createClosedTournament(NAME2, 10, true, true, true)

    cy.log("Check that we cant see the closed tournament")
    cy.checkForNoTournament(NAME2)

  });


});
