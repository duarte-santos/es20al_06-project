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