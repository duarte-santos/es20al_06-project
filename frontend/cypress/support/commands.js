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

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="studentButton"]').click()
})

Cypress.Commands.add('clickOnTournamentsMenu', () => {
    cy.contains('Tournaments').click()
})

Cypress.Commands.add('selectEnrollOnTournamentsMenu', () => {
    cy.contains('Enroll').click()
})

Cypress.Commands.add('goToTournamentEnrollments', () => {
    cy.clickOnTournamentsMenu()
    cy.selectEnrollOnTournamentsMenu()
})

Cypress.Commands.add('enrollInTheTournament', (name) => {
    cy.contains(name).click()
})

Cypress.Commands.add('getTournamentsList', (name) => {
    return cy.get('[data-cy="tournamentsList"]')
})

Cypress.Commands.add('isEnrolledInTheTournament', (name) => {
    cy.contains(name).parent().within(
      () => {
          cy.get('[class="col last-col"]').should(
            (element) => {
                expect(element).to.contain('check')
            }
          )
      }
    )
})

Cypress.Commands.add('isNotEnrolledInTheTournament', (name) => {
    cy.contains(name).parent().within(
      () => {
          cy.get('[class="col last-col"]').should(
            (element) => {
                expect(element).to.not.contain('check')
            }
          )
      }
    )
})

Cypress.Commands.add('checkForTournament', (title) => {
    cy.contains('Tournaments').click()
    cy.contains('Join').click()
    cy.get('[data-cy="tournamentTitle"]').should(
      (element) => {
        expect(element).to.contain(title)
      }
      )

})

Cypress.Commands.add('checkForNoTournament', (title) => {
  cy.contains('Tournaments').click()
  cy.contains('Join').click()
  cy.get('[data-cy="tournamentTitle"]').should(
    (element) => {
      expect(element).to.not.contain(title)
    }
  )
})
