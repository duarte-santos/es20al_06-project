package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SolvedQuizDto implements Serializable {
    private StatementQuizDto statementQuiz;
    private List<CorrectAnswerDto> correctAnswers = new ArrayList<>();
    private String answerDate;
    private int tournamentId;

    public SolvedQuizDto() {
    }

    public SolvedQuizDto(QuizAnswer quizAnswer) {
        this.statementQuiz = new StatementQuizDto(quizAnswer);

        if (quizAnswer.getQuiz().getTournament() == null)
            this.tournamentId = -1;
        else
            this.tournamentId = quizAnswer.getQuiz().getTournament().getId();

        this.correctAnswers = quizAnswer.getQuestionAnswers().stream()
                .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                .map(CorrectAnswerDto::new)
                .collect(Collectors.toList());

        this.answerDate = DateHandler.toISOString(quizAnswer.getAnswerDate());
    }

    public StatementQuizDto getStatementQuiz() {
        return statementQuiz;
    }

    public void setStatementQuiz(StatementQuizDto statementQuiz) {
        this.statementQuiz = statementQuiz;
    }

    public List<CorrectAnswerDto> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<CorrectAnswerDto> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Override
    public String toString() {
        return "SolvedQuizDto{" +
                "statementQuiz=" + statementQuiz +
                ", correctAnswers=" + correctAnswers +
                ", answerDate='" + answerDate + '\'' +
                '}';
    }
}