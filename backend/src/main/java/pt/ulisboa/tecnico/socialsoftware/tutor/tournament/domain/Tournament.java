package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament{

    public enum Status {OPEN, CLOSED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @ManyToMany
    private List<Topic> topicList = new ArrayList<>();

    @ManyToMany
    private List<User> studentList = new ArrayList<>();


    private Integer numberOfQuestions;
    private LocalDateTime startingDate;
    private LocalDateTime conclusionDate;


    @Enumerated(EnumType.STRING)
    private Tournament.Status status;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    public Tournament(){
        this.status = Tournament.Status.CLOSED;
    }


    public Tournament(TournamentDto tournamentDto){

        String titleAux = tournamentDto.getTitle();
        Integer numberOfQuestionsAux = tournamentDto.getNumberOfQuestions();
        LocalDateTime startingDateAux = DateHandler.toLocalDateTime(tournamentDto.getStartingDate());
        LocalDateTime conclusionDateAux = DateHandler.toLocalDateTime(tournamentDto.getConclusionDate());

        Tournament.Status statusAux = tournamentDto.getStatus();



        if (titleAux == null || titleAux.trim().isEmpty()) {
            throw new TutorException(TOURNAMENT_TITLE_IS_EMPTY);
        }

        if (numberOfQuestionsAux <= 0){
            throw new TutorException(TOURNAMENT_NOFQUESTIONS_SMALLER_THAN_1);
        }

        if (startingDateAux == null || conclusionDateAux == null ||
                conclusionDateAux.isBefore(startingDateAux)){
            throw new TutorException(TOURNAMENT_DATES_WRONG_FORMAT);
        }

        if (LocalDateTime.now().isAfter(conclusionDateAux)) {
            throw new TutorException(TOURNAMENT_DATES_WRONG_FORMAT);
        }

        this.title = titleAux;
        this.numberOfQuestions = numberOfQuestionsAux;
        this.startingDate = startingDateAux;
        this.conclusionDate = conclusionDateAux;
        this.status = statusAux;

        //temporary, only while scheduling isn't implemented
        if (LocalDateTime.now().isAfter(startingDateAux) && !(LocalDateTime.now().isAfter(conclusionDateAux))){
            this.status = Status.OPEN;
        }
    }

    public void setTopicDtoList(List<TopicDto> topicList){
        if (topicList == null || topicList.isEmpty()){
            throw new TutorException(TOURNAMENT_TOPIC_LIST_IS_EMPTY);
        }

        for (TopicDto topicdto : topicList){
            Topic topic = new Topic(courseExecution.getCourse(), topicdto);
            this.topicList.add(topic);
            topic.getTournaments().add(this);
        }
    }


    public void updateTopics(Set<Topic> newTopics) {

        newTopics.stream().filter(topic -> !this.topicList.contains(topic)).forEach(topic -> {
            topic.getTournaments().add(this);
            this.topicList.add(topic);
        });
    }


    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public void addStudent(User student){
        studentList.add(student);
    }

    public Status getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Topic> getTopicList() {
        return topicList;
    }



    public void setTopicList(List<Topic> topicList) {
        this.topicList = topicList;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public LocalDateTime getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDateTime startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDateTime getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public List<User> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<User> studentList) {
        this.studentList = studentList;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}

