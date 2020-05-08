describe('edit a question (as a teacher) before making it available walkthrough', () => {
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

  const TOPIC = 'Testability';
  const APPROVED = 'APPROVED';
  const REJECTED = 'REJECTED';

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

  it('[ok] edit a student question before make available', () => {
    cy.evaluateStudentQuestion(TITLE1, APPROVED, JUSTIFICATION);

    // edit and make question available
    cy.makeStudentQuestionAvailableWithEdit(TITLE1, TITLE2, null, [], null);
    //cy.teacherViewQuestions();
    //cy.assertQuestionExists(TITLE1); cypress error? - logout fails after this

    // delete question
    cy.logout();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE1);
  });

  it('[ok] edit a student question topics before make available', () => {
    cy.evaluateStudentQuestion(TITLE1, APPROVED, JUSTIFICATION);

    // edit and make question available
    cy.addStudentQuestionTopic(TITLE1, TOPIC);
    cy.makeStudentQuestionAvailable(TITLE1);
    //cy.teacherViewQuestions();
    //cy.assertQuestionExists(TITLE1); cypress error? - logout fails after this

    // delete question
    cy.logout();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE1);
  });

  it('[nok] edit a non approved student question', () => {
    cy.assertCantMakeSQAvailable(TITLE1);

    // reject question
    cy.evaluateStudentQuestion(TITLE1, REJECTED, JUSTIFICATION);

    // try to edit and make question available
    // to edit the SQ, must first press the 'make available' button
    cy.assertCantMakeSQAvailable(TITLE1);

    // delete question
    cy.logout();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE1);
  });

});
