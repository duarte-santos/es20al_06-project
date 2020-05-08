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

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
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
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);
// ***********************************************
// Tournaments
// ***********************************************

/* ********** Create ********** */



Cypress.Commands.add('gotoCreateTournamentPage', () => {
  cy.contains('Tournaments').click()
  cy.contains('Create').click()
});

/* Create Open Tournament */
Cypress.Commands.add('createOpenTournament', (name, numberOfQuestions) => {

  /* Name */
  cy.get('[data-cy="title"]').type(name)

  /* Starting Date */
  cy.get('[data-cy="startingDate"]').click()
  // Go back 1 month
  cy.get('#startingDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > :nth-child(1)').click()
  cy.wait(500)
  // Choose a day
  cy.get('#startingDateInput-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(11)').click()
  cy.wait(500)
  // Confirm
  cy.get('#startingDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate').click()
  cy.wait(200)

  /* Conclusion Date */
  cy.get('[data-cy="conclusionDate"]').click()
  cy.wait(500)
  // Advance 1 month
  cy.get('#conclusionDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .text-right').click()
  cy.wait(500)
  // Choose a day
  cy.get('#conclusionDateInput-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(11)').click()
  cy.wait(500)
  // Confirm
  cy.get('#conclusionDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate').click()
  cy.wait(500)

  /* Number of Questions */
  cy.get('[data-cy="' + numberOfQuestions + 'questions"]').click()
  cy.wait(200)

  /*Topics*/
  cy.get('.v-input--selection-controls__input')
    .first()
    .click()

  cy.get('[data-cy="submit"]').click()

});

/* Create Closed Tournament */
Cypress.Commands.add('createClosedTournament', (name, numberOfQuestions, startingDate, conclusionDate, selectTopic) => {

  /* Name */
  if (name.length !== 0) {
    cy.get('[data-cy="title"]').type(name)
  }

  /* Starting Date */
  if(startingDate) {
    cy.get('[data-cy="startingDate"]').click()
    // Advance 1 month
    cy.get('#startingDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .text-right').click()
    // Choose a day
    cy.get('.slidenext-enter-active > :nth-child(11)').click()
    // Confirm
    cy.get('#startingDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate').click()
    cy.wait(200)
  }

  /* Conclusion Date */
  if(conclusionDate) {
    cy.get('[data-cy="conclusionDate"]').click()
    cy.wait(500)
    // Advance 2 months
    cy.get('#conclusionDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .text-right').click()
    cy.wait(200)
    cy.get('#conclusionDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .text-right').click()
    cy.wait(500)
    // Choose a day
    cy.get('#conclusionDateInput-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(11)').click()
    // Confirm
    cy.get('#conclusionDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate').click()
    cy.wait(500)
  }

  /* Number of Questions */
  cy.get('[data-cy="' + numberOfQuestions + 'questions"]').click()
  cy.wait(200)

  /*Topics*/
  if (selectTopic) {
    cy.get('.v-input--selection-controls__input')
      .first()
      .click()
  }

  cy.get('[data-cy="submit"]').click()

});

/* Create tournament with conclusion date before starting date */
Cypress.Commands.add('createTournamentWrongDates', (name, numberOfQuestions) => {

  /* Name */
  cy.get('[data-cy="title"]').type(name)

  /* Starting Date */
  cy.get('[data-cy="startingDate"]').click()
  cy.wait(200)
  // Choose a day
  cy.get('#startingDateInput-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(11)').click()
  cy.wait(200)
  // Confirm
  cy.get('#startingDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate').click()
  cy.wait(200)


  /* Conclusion Date */
  cy.get('[data-cy="conclusionDate"]').click()
  //Go back a month
  cy.get('#conclusionDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > :nth-child(1) > .datepicker-button').click()
  // Choose a day before the starting day
  cy.get('.slideprev-enter-active > :nth-child(11)').click()
  // Confirm
  cy.get('#conclusionDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate').click()


  /* Number of Questions */
  cy.get('[data-cy="' + numberOfQuestions + 'questions"]').click()

  /*Topics*/
  cy.get('.v-input--selection-controls__input')
    .first()
    .click()

  cy.get('[data-cy="submit"]').click()

});

/* Assert success or error */

Cypress.Commands.add('assertTournamentCreated', () => {
  cy.get('#success').should('be.visible')
});

Cypress.Commands.add('assertTournamentError', (error) => {
  cy.contains(error)
    .should('be.visible')
    .parent()
    .find('button')
    .click()
});


/* ********** Enroll ********** */

Cypress.Commands.add('goToTournamentEnrollments', () => {
  cy.contains('Tournaments').click()
  cy.contains('Join').click()
});


Cypress.Commands.add('enrollInTournament', (name) => {
  cy.contains(name).parent().within(
    () => {
      cy.get('[class="col last-col"]').children().first().children().first().click()
    })
});

Cypress.Commands.add('assertEnrolled', (name) => {
  cy.contains(name).parent().within(
    () => {
      cy.get('[class="col last-col"]').children().first().children().first().should('not.be.visible')
    })
});

Cypress.Commands.add('shouldCloseConfirmationAlert', () => {
  cy.contains('OK').click()
});

/* ********** Canceling ********** */

Cypress.Commands.add('goToTournamentCanceling', () => {
  cy.contains('Tournaments').click()
  cy.contains('Show All').click()
});


Cypress.Commands.add('assertCanceled', (name) => {
  cy.contains(name).parent().should('not.be.visible')
});

Cypress.Commands.add('cancelTheTournament', (name) => {
  cy.contains(name).parent().within(
    () => {
      cy.get('[class="col last-col"]').children().first().children().first().click()
    })
});


/* ********** Show Open ********** */

Cypress.Commands.add('checkForTournament', (title) => {
  cy.contains('Tournaments').click()
  cy.contains('Show Open').click()
  cy.get('[data-cy="tournamentTitle"]').should(
    (element) => {
      expect(element).to.contain(title)
    })
});

Cypress.Commands.add('checkForNoTournament', (title) => {
  cy.contains('Tournaments').click()
  cy.contains('Show Open').click()
  cy.wait(200)
  cy.get('[data-cy="tournamentTitle"]').should(
    (element) => {
      expect(element).to.not.contain(title)
    })
});

/* ********** Answer Tournament Quiz ********** */

Cypress.Commands.add('answerTournamentQuiz', (title) => {
  cy.contains('Tournaments').click()
  cy.contains('Show Open').click()
  cy.wait(200)
  cy.get('[data-cy="' + title + '.startButton"]').click()
  cy.wait(200)

  /* Answer Questions */
  cy.get('.option-list > :nth-child(1)').click()
  cy.get('.option-list > :nth-child(1)').click()
  cy.get('div.square').click()
  cy.wait(1000)
  cy.get('.option-list > :nth-child(2)').click()
  cy.get('div.square').click()
  cy.wait(1000)
  cy.get('.option-list > :nth-child(3)').click()
  cy.get('div.square').click()
  cy.wait(1000)
  cy.get('.option-list > :nth-child(4)').click()
  cy.get('div.square').click()
  cy.wait(1000)
  cy.get('.option-list > :nth-child(1)').click()

  cy.wait(1000)
  cy.get('.end-quiz').click()
  cy.wait(200)
  cy.get('.primary--text').click()

});

Cypress.Commands.add('checkTournamentQuizAnswered', (title) => {
  cy.contains('Tournaments').click()
  cy.contains('Show Open').click()
  cy.wait(200)
  cy.get('[data-cy="' + title + '.startButton"]').should('not.exist')
});
/* ********** Dashboard ********** */

Cypress.Commands.add('goToDashboard', () => {
  cy.contains('Tournaments').click()
  cy.contains('Dashboard').click()
});

Cypress.Commands.add('checkTournamentInDashboard', (title) => {
  cy.get('[data-cy="tournamentTitle"]').should(
    (element) => {
      expect(element).to.contain(title)
    })
});


/* ****** Create Tournament with Different Student ****** */

Cypress.Commands.add('createTournamentDifferentStudent', (id, title, creator) => {
  // Create tournament
  cy.exec('PGPASSWORD=c3 psql -d tutordb -U c3 -h localhost -c ' +
    '"INSERT INTO tournaments (id, starting_date, conclusion_date, number_of_questions, title, course_execution_id, creator_id) ' +
    'VALUES (' + id + ', \'2018-04-28 05:32:00\', \'2100-04-28 05:32:00\', 5,\'' + title + '\', 11,' + creator + ');"')

  // Enroll creator in the tournament
  cy.exec('PGPASSWORD=c3 psql -d tutordb -U c3 -h localhost -c ' +
    '"INSERT INTO tournaments_student_list (tournaments_enrolled_id, student_list_id) ' +
    'VALUES (' + id + ',' + creator + ');"')

  // Add topics
  cy.exec('PGPASSWORD=c3 psql -d tutordb -U c3 -h localhost -c ' +
    '"INSERT INTO tournaments_topic_list (tournaments_id, topic_list_id) ' +
    'VALUES (' + id + ',100);"')
});

/* ********** Close a Tournament ********** */
Cypress.Commands.add('closeTournament', (id) => {
  // Create tournament
  cy.exec('PGPASSWORD=c3 psql -d tutordb -U c3 -h localhost -c ' +
    '"UPDATE tournaments\n' +
    'SET conclusion_date = \'2018-04-29 05:32:00\' ' +
    'WHERE id = ' + id + '; "')
});

/* ********** Delete Tournament from DB ********** */

Cypress.Commands.add('deleteTournament', (id) => {
  cy.exec('PGPASSWORD=c3 psql -d tutordb -U c3 -h localhost -c ' +
    '"DELETE FROM tournaments_topic_list WHERE tournaments_id =' + id + '; ' +
    ' DELETE FROM tournaments_student_list WHERE tournaments_enrolled_id =' + id + '; ' +
    ' DELETE FROM tournaments_answered_list WHERE tournaments_answered_id =' + id + '; ' +
    ' DELETE FROM tournaments WHERE id=' + id + '; "')
});


// ***********************************************
// Student Question
// ***********************************************

Cypress.Commands.add('teacherViewQuestions', () => {
  cy.contains('Management').click();
  cy.contains('Questions').click();
});

Cypress.Commands.add('studentMyQuestions', () => {
  cy.contains('My Questions').click();
});

Cypress.Commands.add('teacherEvaluateQuestions', () => {
  cy.contains('Management').click();
  cy.contains('Evaluate Questions').click();
});

function editAndSave(title, content, optionList, correct) {
  if (title) {
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

Cypress.Commands.add(
  'createStudentQuestion',
  (title, content, optionList, correct) => {
    cy.get('[data-cy="createButton"]').click();
    editAndSave(title, content, optionList, correct);
  }
);

function findRowAndClick(title, dataCy, elementCount) {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', elementCount)
    .find(`[data-cy="${dataCy}"]`)
    .click();
}

Cypress.Commands.add('deleteStudentQuestion', title => {
  findRowAndClick(title, 'delete', 7);
});

Cypress.Commands.add('addStudentQuestionTopic', (title, topic) => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .find('[data-cy="topics"]')
    .type(topic)
    .type('{enter}');
});

Cypress.Commands.add(
  'evaluateStudentQuestion',
  (title, state, justification) => {
    findRowAndClick(title, 'evaluate', 8);
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

Cypress.Commands.add('closeException', () => {
  cy.get('.v-alert__icon')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('makeStudentQuestionAvailable', title => {
  findRowAndClick(title, 'available', 8);
  editAndSave(null, null, [], null);
});

Cypress.Commands.add(
  'makeStudentQuestionAvailableWithEdit',
  (oldTitle, title, content, optionList, correct) => {
    findRowAndClick(oldTitle, 'available', 8);
    editAndSave(title, content, optionList, correct);
  }
);

Cypress.Commands.add('assertCantMakeSQAvailable', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="available"]')
    .should('not.exist');
});

Cypress.Commands.add('assertQuestionExists', title => {
  cy.contains(title)
    .parent()
    .should('exist');
});

Cypress.Commands.add(
  'editStudentQuestion',
  (oldTitle, title, content, optionList, correct) => {
    findRowAndClick(oldTitle, 'edit', 7);
    editAndSave(title, content, optionList, correct);
  }
);

Cypress.Commands.add('editNotAvailable', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="edit"]')
    .should('not.exist');
});

Cypress.Commands.add('gotoSQDashboard', () => {
  cy.contains('Dashboard').click();
  cy.get('[data-cy="question-dashboard"]').click();
});
