describe('Edit StudentQuestion walkthrough', () => {
  const APPROVED = 'APPROVED';
  const REJECTED = 'REJECTED';

  const TITLE1 = 'CYPRESS-TEST-TITLE-1';
  const TITLE2 = 'CYPRESS-TEST-TITLE-2';
  const CONTENT = 'CYPRESS-TEST-CONTENT';
  const CORRECT = 4;
  const JUSTIFICATION = 'CYPRESS-TEST-JUSTIFICATION';

  const OPTIONS = [
    'CYPRESS-TEST-OPTION1',
    'CYPRESS-TEST-OPTION2',
    'CYPRESS-TEST-OPTION3',
    'CYPRESS-TEST-OPTION4'
  ];

  beforeEach(() => {
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, CORRECT);
    cy.logout();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();
  });

  afterEach(() => {
    cy.logout();
  });

  it('[ok] edit a rejected student question', () => {
    cy.evaluateStudentQuestion(TITLE1, REJECTED, JUSTIFICATION);
    cy.logout();

    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.editStudentQuestion(TITLE1, TITLE2, null, [], null);

    // assert
    cy.deleteStudentQuestion(TITLE2);
  });

  it('[nok] edit a non rejected student question', () => {
    cy.evaluateStudentQuestion(TITLE1, APPROVED, JUSTIFICATION);
    cy.logout();

    cy.demoStudentLogin();
    cy.studentMyQuestions();

    // assert
    cy.editNotAvailable(TITLE1);

    cy.deleteStudentQuestion(TITLE1);
  });

  it('[nok] edit a student question with the same data', () => {
    cy.evaluateStudentQuestion(TITLE1, REJECTED, JUSTIFICATION);
    cy.logout();

    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.editStudentQuestion(TITLE1, null, null, [], null);

    // assert
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.deleteStudentQuestion(TITLE1);
  });
});
