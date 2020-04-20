describe('StudentQuestion walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.studentMyQuestions();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  const TITLE1 = 'TEST-TITLE1';
  const CONTENT = 'TEST-CONTENT';
  const CORRECT = 4;
  const JUSTIFICATION = 'TEST-JUSTIFICATION';

  const OPTIONS = [
    'TEST-OPTION1',
    'TEST-OPTION2',
    'TEST-OPTION3',
    'TEST-OPTION4'
  ];

  const TITLE2 = 'TEST-TITLE2';
  const TITLE3 = 'TEST-TITLE3';

  const TOPIC = 'Testability';
  const APPROVED = 'APPROVED';
  const REJECTED = 'REJECTED';

  it('[ok] creates and deletes a student question', () => {
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, CORRECT);
    cy.addStudentQuestionTopic(TITLE1, TOPIC);
    cy.deleteStudentQuestion(TITLE1);
  });

  it('[nok] try to create a student question with wrong input', () => {
    cy.log('try to create with no title');
    cy.createStudentQuestion(null, CONTENT, OPTIONS, CORRECT);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.log('try to create with no content');
    cy.createStudentQuestion(TITLE1, null, OPTIONS, CORRECT);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.log('try to create without all options');
    cy.createStudentQuestion(TITLE1, CONTENT, [], CORRECT);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.log('try to create without a correct option');
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, null);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('[ok] create a student question, teacher evaluates the question', () => {
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, CORRECT);

    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();

    cy.evaluateStudentQuestion(TITLE1, APPROVED, JUSTIFICATION);

    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE1);
  });

  it('[ok] creates 3 student questions, teacher evaluates 2, student views the evaluations', () => {
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, CORRECT);
    cy.createStudentQuestion(TITLE2, CONTENT, OPTIONS, CORRECT);
    cy.createStudentQuestion(TITLE3, CONTENT, OPTIONS, CORRECT);

    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();

    cy.evaluateStudentQuestion(TITLE2, REJECTED, JUSTIFICATION);
    cy.evaluateStudentQuestion(TITLE1, APPROVED, null);

    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE3);
    cy.deleteStudentQuestion(TITLE2);
    cy.deleteStudentQuestion(TITLE1);
  });

  it('[nok] creates a student questions, try to evaluate question with wrong input', () => {
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, CORRECT);

    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();

    cy.log('try to reject with no justification');
    cy.evaluateStudentQuestion(TITLE1, REJECTED, null);
    cy.closeException();
    cy.get('[data-cy="cancelButton"]').click();

    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE1);
  });
});
