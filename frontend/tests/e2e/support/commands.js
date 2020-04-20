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
const USER = 'duarte';
const PASSWORD = 'bheu0325';

Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="adminButton"]').click();
  cy.contains('Administration').click();
  cy.contains('Manage Courses').click();
});

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="Name"]').type(name);
  cy.get('[data-cy="Acronym"]').type(acronym);
  cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

// ***********************************************
// Student Question
// ***********************************************

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="studentButton"]').click();
});

Cypress.Commands.add('studentMyQuestions', () => {
  cy.contains('My Questions').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="teacherButton"]').click();
});

Cypress.Commands.add('teacherEvaluateQuestions', () => {
  cy.contains('Management').click();
  cy.contains('Evaluate Questions').click();
});

Cypress.Commands.add(
  'createStudentQuestion',
  (title, content, optionList, correct) => {
    cy.get('[data-cy="createButton"]').click();
    if (title) {
      //cy.get('.v-dialog').scrollTo('top');
      cy.get('[data-cy="title"]').type(title);
    }
    if (content) {
      cy.get('[data-cy="content"]').type(content);
    }
    for (let i = 0; i < optionList.length; i++) {
      cy.get(`[data-cy="option${i}"]`).type(optionList[i]);
    }
    if (correct) {
      cy.get(`[data-cy="radio${correct}"]`)
        .parent()
        .click();
    }
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('deleteStudentQuestion', (title, optionList) => {
  const command =
    `DELETE FROM student_question_options WHERE options in (\'${optionList[0]}\',\'${optionList[1]}\', \'${optionList[2]}\',\'${optionList[3]}\');` +
    `DELETE FROM student_questions WHERE title=\'${title}\';`;

  cy.exec(
    `PGPASSWORD=${PASSWORD} psql -d tutordb -U ${USER} -c \"${command}\"`
  );
});

Cypress.Commands.add('deleteStudentQuestionTopic', topic => {
  const command = `DELETE FROM student_question_topics WHERE topics=\'${topic}\';`;

  cy.exec(
    `PGPASSWORD=${PASSWORD} psql -d tutordb -U ${USER} -c \"${command}\"`
  );
});

Cypress.Commands.add('addStudentQuestionTopic', (title, topic) => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 6)
    .find('[data-cy="topics"]')
    .type(topic)
    .type('{enter}');
});

Cypress.Commands.add(
  'evaluateStudentQuestion',
  (title, state, justification) => {
    cy.contains(title)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="evaluate"]')
      .click();

    if (state) {
      cy.get(`[data-cy="${state}"]`)
        .parent()
        .click();
    }
    if (justification) {
      cy.get('[data-cy="justification"]').type(justification);
    }
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('deleteQuestion', (title, optionList) => {
  const command =
    `DELETE FROM options WHERE content in (\'${optionList[0]}\',\'${optionList[1]}\', \'${optionList[2]}\',\'${optionList[3]}\');\n` +
    `DELETE FROM questions WHERE title=\'${title}\';`;

  cy.exec(
    `PGPASSWORD=${PASSWORD} psql -d tutordb -U ${USER} -c \"${command}\"`
  );
});

Cypress.Commands.add('closeException', () => {
  cy.get('.v-alert__icon')
    .parent()
    .find('button')
    .click();
});
