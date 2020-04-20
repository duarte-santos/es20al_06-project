describe('StudentQuestion walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.studentMyQuestions();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  const TITLE = 'TEST-TITLE';
  const CONTENT = 'TEST-CONTENT';
  const CORRECT = 4;
  const JUSTIFICATION = 'TEST-JUSTIFICATION';

  const OPTIONS = [
    'TEST-OPTION1',
    'TEST-OPTION2',
    'TEST-OPTION3',
    'TEST-OPTION4'
  ];

  const TITLE2 = 'TEST-TITLE-2';
  const TITLE3 = 'TEST-TITLE-3';

  const TOPIC = 'Testability';
  const APPROVED = 'APPROVED';
  const REJECTED = 'REJECTED';

  it('[ok] creates and deletes a student question', () => {
    cy.createStudentQuestion(TITLE, CONTENT, OPTIONS, CORRECT);
    cy.addStudentQuestionTopic(TITLE, TOPIC);
    cy.deleteStudentQuestionTopic(TOPIC);
    cy.deleteStudentQuestion(TITLE, OPTIONS);
  });

  it('[nok] try to create a student question with wrong input', () => {
    cy.log('try to create with no title');
    cy.createStudentQuestion(null, CONTENT, OPTIONS, CORRECT);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.log('try to create with no content');
    cy.createStudentQuestion(TITLE, null, OPTIONS, CORRECT);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.log('try to create without all options');
    cy.createStudentQuestion(TITLE, CONTENT, [], CORRECT);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.log('try to create without a correct option');
    cy.createStudentQuestion(TITLE, CONTENT, OPTIONS, null);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('[ok] create a student question, teacher evaluates the question', () => {
    cy.createStudentQuestion(TITLE, CONTENT, OPTIONS, CORRECT);

    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();

    cy.evaluateStudentQuestion(TITLE, APPROVED, JUSTIFICATION);

    cy.deleteQuestion(TITLE, OPTIONS);
    cy.deleteStudentQuestion(TITLE, OPTIONS);
  });

  it('[ok] creates 3 student questions, teacher evaluates 2, student views the evaluations', () => {
    cy.createStudentQuestion(TITLE, CONTENT, OPTIONS, CORRECT);
    cy.createStudentQuestion(TITLE2, CONTENT, OPTIONS, CORRECT);
    cy.createStudentQuestion(TITLE3, CONTENT, OPTIONS, CORRECT);

    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();

    cy.evaluateStudentQuestion(TITLE, APPROVED, null);
    cy.evaluateStudentQuestion(TITLE2, REJECTED, JUSTIFICATION);

    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.studentMyQuestions();

    cy.deleteQuestion(TITLE, OPTIONS);
    cy.deleteStudentQuestion(TITLE, OPTIONS);
    cy.deleteStudentQuestion(TITLE2, OPTIONS);
    cy.deleteStudentQuestion(TITLE3, OPTIONS);
  });

  it('[nok] creates a student questions, try to evaluate question with wrong input', () => {
    cy.createStudentQuestion(TITLE, CONTENT, OPTIONS, CORRECT);

    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();

    cy.log('try to reject with no justification');
    cy.evaluateStudentQuestion(TITLE, REJECTED, null);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.deleteStudentQuestion(TITLE, OPTIONS);
  });
});
