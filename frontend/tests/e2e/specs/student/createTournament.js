describe('Administration walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })



  it('login create an open tournament', () => {
    cy.gotoCreateTournamentPage()
    cy.createOpenTournament('HighwayToHell', 15, true, true, true)
    cy.assertTournamentCreated()
  });

  it('login create a closed tournament', () => {
    cy.gotoCreateTournamentPage()
    cy.createClosedTournament('FadeToBlack', 15, true, true, true)
    cy.assertTournamentCreated()
  });





  it('login create a tournament without title', () => {
    cy.gotoCreateTournamentPage()

    cy.log('try to create a tournament without title')
    cy.createClosedTournament('', 15, true, true, true)

    cy.assertTournamentError('Error: The tournament must have a title')
  });




  it('login create a tournament without starting date', () => {
    cy.gotoCreateTournamentPage()

    cy.log('try to create a tournament without starting date')
    cy.createClosedTournament('DancingShoes', 15, false, true, true)

    cy.assertTournamentError('Error: The tournament must have a starting date')
  });




  it('login create a tournament without conclusion date', () => {
    cy.gotoCreateTournamentPage()

    cy.log('try to create a tournament without conclusion date')
    cy.createClosedTournament('Everlong', 15, true, false, true)

    cy.assertTournamentError('Error: The tournament must have a conclusion date')
  });




  it('login create a tournament without selecting any topic', () => {
    cy.gotoCreateTournamentPage()

    cy.log('try to create a tournament without topics')
    cy.createClosedTournament('Otherside', 15, true, true, false)

    cy.assertTournamentError('Error: You must select at least 1 topic')
  });




  it('login create a tournament with conclusion date before starting date', () => {
    cy.gotoCreateTournamentPage()

    cy.log('try to create a tournament with conclusion date before starting date')
    cy.createTournamentWrongDates('ParadiseCity', 15)

    cy.assertTournamentError('Error: The tournament has a wrong date format')

  });

});
