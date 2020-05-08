describe('make StudentQuestion available walkthrough', () => {
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

  const TOPIC1 = 'Testability';
  const TOPIC2 = 'Availability';
  const APPROVED = 'APPROVED';
  const REJECTED = 'REJECTED';

  beforeEach(() => {
    cy.demoStudentLogin();
    cy.studentMyQuestions();
  });

  afterEach(() => {
    cy.logout();
  });

  it('[ok] teacher approves and makes a student question with 2 topics available', () => {
    // create question
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, CORRECT);
    cy.addStudentQuestionTopic(TITLE1, TOPIC1);
    cy.addStudentQuestionTopic(TITLE1, TOPIC2);

    // approve question
    cy.logout();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();
    cy.evaluateStudentQuestion(TITLE1, APPROVED, JUSTIFICATION);

    // make question available
    cy.makeStudentQuestionAvailable(TITLE1);
    //cy.teacherViewQuestions();
    //cy.assertQuestionExists(TITLE1); cypress error? - logout fails after this

    // delete question
    cy.logout();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE1);
  });

  it('[ok] teacher approves and makes 2 student questions available', () => {
    // create questions
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, CORRECT);
    cy.createStudentQuestion(TITLE2, CONTENT, OPTIONS, CORRECT);

    // approve questions
    cy.logout();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();
    cy.evaluateStudentQuestion(TITLE1, APPROVED, null);
    cy.evaluateStudentQuestion(TITLE2, APPROVED, null);

    // make questions available
    cy.makeStudentQuestionAvailable(TITLE1);
    cy.makeStudentQuestionAvailable(TITLE2);
    //cy.teacherViewQuestions();
    //cy.assertQuestionExists(TITLE1);
    //cy.assertQuestionExists(TITLE2);

    // delete questions
    cy.logout();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE1);
    cy.deleteStudentQuestion(TITLE2);
  });

  it('[nok] teacher rejects student question, tries to make it available', () => {
    // create question
    cy.createStudentQuestion(TITLE1, CONTENT, OPTIONS, CORRECT);

    // try to make question available
    cy.logout();
    cy.demoTeacherLogin();
    cy.teacherEvaluateQuestions();
    cy.assertCantMakeSQAvailable(TITLE1);

    // reject question
    cy.evaluateStudentQuestion(TITLE1, REJECTED, JUSTIFICATION);

    // try to make question available
    cy.assertCantMakeSQAvailable(TITLE1);

    // delete question
    cy.logout();
    cy.demoStudentLogin();
    cy.studentMyQuestions();
    cy.deleteStudentQuestion(TITLE1);
  });
});
