describe('Enroll in a Tournament Walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  const NAME1 = 'Subaru90';

  afterEach(() => {
     cy.contains('Demo Course').click()
     cy.contains('Logout').click()
    //should delete after each, but we don't have a functionality for it
  })

  it('login, enroll in a tournament', () => {
    cy.gotoCreateTournamentPage()
    cy.createClosedTournament(NAME1, 25, true, true, true)
    cy.goToTournamentEnrollments()

    cy.log("Enroll in a tournament")
    cy.enrollInTournament(NAME1)

    cy.assertEnrolled(NAME1)
  });

  it('login, try to enroll in the same tournament as before', () => {

    cy.goToTournamentEnrollments()

    cy.log("Try to enroll in the same tournament as before (Should throw an error)")
    cy.enrollInTournament(NAME1)

    cy.assertTournamentError("Error: The student has signed up for this tournament before")
  });

});
