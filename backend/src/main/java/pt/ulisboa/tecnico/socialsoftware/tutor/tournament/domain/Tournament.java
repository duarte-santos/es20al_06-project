package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament{

    public enum Status {OPEN, CLOSED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @ManyToOne
    private User creatingUser;

    @ManyToMany(mappedBy = "tournaments")
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

        String title = tournamentDto.getTitle();
        Integer numberOfQuestions = tournamentDto.getNumberOfQuestions();
        LocalDateTime startingDate = tournamentDto.getStartingDateDate();
        LocalDateTime conclusionDate = tournamentDto.getConclusionDateDate();
        Tournament.Status status = tournamentDto.getStatus();



        if (title == null || title.trim().isEmpty()) {
            throw new TutorException(TOURNAMENT_TITLE_IS_EMPTY);
        }

        if (numberOfQuestions <= 0){
            throw new TutorException(TOURNAMENT_NOFQUESTIONS_SMALLER_THAN_1);
        }

        if (startingDate == null || conclusionDate == null ||
                conclusionDate.isBefore(startingDate)){
            throw new TutorException(TOURNAMENT_DATES_WRONG_FORMAT);
        }

        if (LocalDateTime.now().isAfter(startingDate)){
            throw new TutorException(TOURNAMENT_DATES_WRONG_FORMAT);
        }

        this.title = title;
        this.numberOfQuestions = numberOfQuestions;
        this.startingDate = startingDate;
        this.conclusionDate = conclusionDate;
        this.status = status;
    }

    public void setTopicDtoList(List<TopicDto> topicList){
        if (topicList == null || topicList.isEmpty()){
            throw new TutorException(TOURNAMENT_TOPIC_LIST_IS_EMPTY);
        }

        for (TopicDto topicdto : topicList){
            Topic topic = new Topic(courseExecution.getCourse(), topicdto);
            this.topicList.add(topic);
        }
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

    public User getCreatingUser() {
        return creatingUser;
    }

    public void setCreatingUser(User creatingUser) {
        this.creatingUser = creatingUser;
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

