package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    COURSE_NOT_FOUND("Course not found with name %s"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %d not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    TOURNAMENT_TITLE_IS_EMPTY("The tournament title is empty"),
    TOURNAMENT_CREATOR_DOESNT_EXIST("The user that creates the tournament doesnt exist"),
    TOURNAMENT_TOPIC_LIST_IS_EMPTY("The tournament topicList is empty"),
    TOURNAMENT_NOFQUESTIONS_SMALLER_THAN_1("The tournament has an invalid number of questions"),
    TOURNAMENT_DATES_WRONG_FORMAT("The tournament has a wrong date format"),
    TOURNAMENT_TOPIC_DOESNT_EXIST("The tournament topicList has a topic that doesnt exist"),
    TOURNAMENT_NOT_FOUND("The tournament doesn't exist"),
    TOURNAMENT_IS_CLOSED("The tournament is closed"),
    TOURNAMENT_IS_OPEN("Can not cancel open tournaments"),
    NO_OPEN_TOURNAMENTS("There are no open tournaments"),
    STUDENT_ALREADY_ENROLLED("The student has signed up for this tournament before"),
    STUDENT_NOT_ENROLLED(" The student is not enrolled in this tournament"),
    TOURNAMENT_NOT_OPEN(" The tournament has not opened yet"),
    TOURNAMENT_IS_FINISHED("The tournament is already finished"),
    QUIZ_CANT_BE_GENERATED("The tournament quiz can't be generated"),
    STUDENT_ALREADY_PARTICIPATED("The student has already participated in this tournament"),

    STUDENT_QUESTION_NOT_FOUND("Student's question not found with id %d"),
    STUDENT_QUESTION_ALREADY_EVALUATED("Student's question has already been evaluated"),
    JUSTIFICATION_MISSING_DATA("Rejected questions must have a justification"),
    STUDENT_QUESTION_INVALID_OPTIONS_AMOUNT("Questions must have 4 options"),
    SQ_TITLE_MISSING_DATA("Student's question titles cannot be blank"),
    SQ_CONTENT_MISSING_DATA("Student's question contents cannot be blank"),
    SQ_OPTION_MISSING_DATA("Student's question options cannot be blank"),
    SQ_INVALID_CORRECT_OPTION("Questions must have 1 correct option"),
    CANNOT_EDIT_STUDENT_QUESTION("Student's question can only be edited if rejected"),
    SQ_ALREADY_AVAILABLE("Student Question is already part of the question pool"),
    SQ_CANNOT_BECOME_QUESTION("Student Question has to be approved before becoming a Question"),
    EDIT_DATA_STUDENT_QUESTION("Cannot edit a Student Question with the same data"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),

    ACCESS_DENIED("You do not have permission to view this resource"),
    CANNOT_OPEN_FILE("Cannot open file");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}