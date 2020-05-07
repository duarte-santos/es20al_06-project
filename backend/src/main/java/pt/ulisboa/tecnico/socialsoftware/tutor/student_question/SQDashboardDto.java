package pt.ulisboa.tecnico.socialsoftware.tutor.student_question;

import java.io.Serializable;

public class SQDashboardDto implements Serializable {
    private Integer totalQuestions = 0;
    private Integer totalApprovedQuestions = 0;

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getTotalApprovedQuestions() {
        return totalApprovedQuestions;
    }

    public void setTotalApprovedQuestions(Integer totalApprovedQuestions) {
        this.totalApprovedQuestions = totalApprovedQuestions;
    }

}
