describe('Enroll in a Tournament', () => {
  beforeEach(() => {
    /*
    it's assumed the database has the following data:

psql --dbname=tutordb --username=? --password <<EOF

--create tournaments
INSERT INTO tournaments VALUES (1, '2020-08-08', 3, '2020-08-01', 'OPEN', 'first', 11, 651);
INSERT INTO tournaments VALUES (2, '2020-08-09', 4, '2020-08-02', 'OPEN', 'second', 11, 616);
INSERT INTO tournaments VALUES (3, '2020-08-10', 5, '2020-08-03', 'OPEN', 'third', 11, 676);

--"enroll" Demo Student in one tournament
INSERT INTO users_tournaments_enrolled VALUES (676, 3, 676);

--clean up
--DELETE FROM users_tournaments_enrolled WHERE user_id = 676;
--DELETE FROM tournaments WHERE id IN (1, 2, 3);
EOF

    **/

    cy.demoStudentLogin()
    cy.goToTournamentEnrollments()
  })

  afterEach(() => {
     cy.contains('Logout').click()
  })

  it('Enroll in a tournament', () => {
    let name = 'first';

    cy.isNotEnrolledInTheTournament(name)
    cy.enrollInTheTournament(name)
    cy.isEnrolledInTheTournament(name)
  });

  it('Try to enroll in a tournament while already enrolled', () => {
    let name = 'third';

    cy.isEnrolledInTheTournament(name)
    cy.enrollInTheTournament(name)
    cy.closeErrorMessage()

  });

});
