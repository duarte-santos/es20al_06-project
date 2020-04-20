// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />
Cypress.Commands.add('demoAdminLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="adminButton"]').click()
    cy.contains('Administration').click()
    cy.contains('Manage Courses').click()
})

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="Name"]').type(name)
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
    cy.contains('Error')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy.contains(acronym)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteCourse"]')
        .click()
})

Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="createFromCourse"]')
        .click()
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})


/* TOURNAMENTS */

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="studentButton"]').click()
})

Cypress.Commands.add('gotoCreateTournamentPage', () => {
  cy.contains('Tournaments').click()
  cy.contains('Create').click()
})

/* Create Open Tournament */
Cypress.Commands.add('createOpenTournament', (name, numberOfQuestions) => {

  /* Name */
  cy.get('[data-cy="title"]').type(name)

  /* Starting Date */
  cy.get('[data-cy="startingDate"]').click()
  cy.get('.v-date-picker-header > :nth-child(1) > .v-btn__content > .v-icon').click()
  cy.get('.v-date-picker-header > :nth-child(1) > .v-btn__content > .v-icon').click()
  cy.get(':nth-child(5) > :nth-child(7) > .v-btn:visible').click()
  cy.get('.green--text > .v-btn__content').click()

  /* Conclusion Date */
  cy.get('[data-cy="conclusionDate"]').click()
  cy.get('.v-date-picker-header > :nth-child(3) > .v-btn__content > .v-icon:visible').click()
  cy.get(':nth-child(5) > :nth-child(7) > .v-btn:visible').click()
  cy.get('.green--text > .v-btn__content:visible').click()

  /* Number of Questions */
  cy.get('[data-cy="' + numberOfQuestions + 'questions"]').click()

  /*Topics*/
  cy.get('.v-input--selection-controls__input')
    .first()
    .click()

  cy.contains('SUBMIT').click()

})

/* Create Closed Tournament */
Cypress.Commands.add('createClosedTournament', (name, numberOfQuestions, startingDate, conclusionDate, selectTopic) => {

  /* Name */
  if (name.length !== 0) {
    cy.get('[data-cy="title"]').type(name)
  }

  /* Starting Date */
  if (startingDate) {
    cy.get('[data-cy="startingDate"]').click()
    cy.get('.v-date-picker-header > :nth-child(3) > .v-btn__content > .v-icon').click()
    cy.get(':nth-child(5) > :nth-child(6) > .v-btn').click()
    cy.get('.green--text > .v-btn__content').click()
  }

  /* Conclusion Date */
  if (conclusionDate) {
    cy.get('[data-cy="conclusionDate"]').click()
    cy.get('.v-date-picker-header > :nth-child(3) > .v-btn__content > .v-icon:visible').click()
    cy.get(':nth-child(5) > :nth-child(7) > .v-btn:visible').click()
    cy.get('.green--text > .v-btn__content:visible').click()
  }

  /* Number of Questions */
  cy.get('[data-cy="' + numberOfQuestions + 'questions"]').click()

  /*Topics*/
  if (selectTopic) {
    cy.get('.v-input--selection-controls__input')
      .first()
      .click()
  }

  cy.contains('SUBMIT').click()

})

/* Create tournament with conclusion date before starting date */
Cypress.Commands.add('createTournamentWrongDates', (name, numberOfQuestions) => {

  /* Name */
  cy.get('[data-cy="title"]').type(name)

  /* Starting Date */
  cy.get('[data-cy="startingDate"]').click()
  cy.get('.v-date-picker-header > :nth-child(3) > .v-btn__content > .v-icon').click()
  cy.get(':nth-child(5) > :nth-child(7) > .v-btn').click()
  cy.get('.green--text > .v-btn__content').click()

  /* Conclusion Date */
  cy.get('[data-cy="conclusionDate"]').click()
  cy.get('.v-date-picker-header > :nth-child(3) > .v-btn__content > .v-icon:visible').click()
  cy.get(':nth-child(5) > :nth-child(6) > .v-btn:visible').click()
  cy.get('.green--text > .v-btn__content:visible').click()

  /* Number of Questions */
  cy.get('[data-cy="' + numberOfQuestions + 'questions"]').click()

  /*Topics*/
  cy.get('.v-input--selection-controls__input')
    .first()
    .click()

  cy.contains('SUBMIT').click()

})

/* Assert success or error */

Cypress.Commands.add('assertTournamentCreated', () => {
  cy.get('#success').should('be.visible')
})

Cypress.Commands.add('assertTournamentError', (error) => {
  cy.contains(error)
    .should('be.visible')
    .parent()
    .find('button')
    .click()
})


/* Enroll */

Cypress.Commands.add('clickOnTournamentsMenu', () => {
    cy.contains('Tournaments').click()
})

Cypress.Commands.add('selectEnrollOnTournamentsMenu', () => {
    cy.contains('Join').click()
})

Cypress.Commands.add('goToTournamentEnrollments', () => {
    cy.clickOnTournamentsMenu()
    cy.selectEnrollOnTournamentsMenu()
})


Cypress.Commands.add('tryToEnrollInTournament', (name) => {
  cy.contains(name).parent().within(
    () => {
      cy.get('[class="col last-col"]').children().first().children().first().click()
    })
})



Cypress.Commands.add('shouldCloseConfirmationAlert', () => {
    cy.contains('OK').click()
})



/* Show Open */

Cypress.Commands.add('checkForTournament', (title) => {
    cy.contains('Tournaments').click()
    cy.contains('Open').click()
    cy.get('[data-cy="tournamentTitle"]').should(
      (element) => {
        expect(element).to.contain(title)
        })
})

Cypress.Commands.add('checkForNoTournament', (title) => {
  cy.contains('Tournaments').click()
  cy.contains('Open').click()
  cy.wait(200)
  cy.get('[data-cy="tournamentTitle"]').should(
    (element) => {
      expect(element).to.not.contain(title)
    })
})



