package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.util.List;

@RestController
public class StudentQuestionController {

    private StudentQuestionService studentQuestionService;

    StudentQuestionController(StudentQuestionService studentQuestionService) {
        this.studentQuestionService = studentQuestionService;
    }

    @PostMapping("/courses/{courseId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto createStudentQuestion(@PathVariable int courseId, Authentication authentication,
                                                    @Valid @RequestBody StudentQuestionDto studentQuestionDto) {

        Integer studentId = ((User) authentication.getPrincipal()).getId();
        return this.studentQuestionService.createStudentQuestion(courseId, studentId, studentQuestionDto);
    }

    @PutMapping("/studentQuestions/{studentQuestionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#studentQuestionId, 'STUDENT_QUESTION.ACCESS')")
    public StudentQuestionDto evaluateStudentQuestion(@PathVariable Integer studentQuestionId,
                                                      @Valid @RequestBody StudentQuestionDto studentQuestionDto) {

        return this.studentQuestionService.evaluateStudentQuestion(studentQuestionId, studentQuestionDto);
    }

    @GetMapping("/studentQuestions/ownQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<StudentQuestionDto> viewOwnStudentQuestions(Authentication authentication) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();
        return this.studentQuestionService.viewOwnStudentQuestions(studentId);
    }

}