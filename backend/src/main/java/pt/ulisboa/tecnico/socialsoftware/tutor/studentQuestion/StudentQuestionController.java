package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@RestController
public class StudentQuestionController {
    private static Logger logger = LoggerFactory.getLogger(StudentQuestionController.class);

    private StudentQuestionService studentQuestionService;

    @Value("${figures.dir}")
    private String figuresDir;

    StudentQuestionController(StudentQuestionService studentQuestionService) {
        this.studentQuestionService = studentQuestionService;
    }

    @GetMapping("/courses/{courseId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getCourseStudentQuestions(@PathVariable int courseId){
        return this.studentQuestionService.findStudentQuestions(courseId);
    }

    @PostMapping("/courses/{courseId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto createStudentQuestion(@PathVariable int courseId, Authentication authentication,
                                                    @Valid @RequestBody StudentQuestionDto studentQuestionDto) {

        Integer studentId = ((User) authentication.getPrincipal()).getId();
        return this.studentQuestionService.createStudentQuestion(courseId, studentId, studentQuestionDto);
    }

    @PutMapping("/studentQuestions/{studentQuestionId}/evaluate")
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

    @PutMapping("/studentQuestions/{studentQuestionId}/topics")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity updateStudentQuestionTopics(@PathVariable Integer studentQuestionId, @RequestBody String[] topics) {
        studentQuestionService.updateStudentQuestionTopics(studentQuestionId, topics);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/studentQuestions/{studentQuestionId}/image")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String updateStudentQuestionImage(@PathVariable Integer studentQuestionId, @RequestParam("file") MultipartFile file) throws IOException {
        logger.debug("uploadImage  studentQuestionId: {}: , filename: {}", studentQuestionId, file.getContentType());

        StudentQuestionDto studentQuestionDto = studentQuestionService.findStudentQuestionById(studentQuestionId);
        String url = studentQuestionDto.getImage() != null ? studentQuestionDto.getImage().getUrl() : null;
        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        int lastIndex = Objects.requireNonNull(file.getContentType()).lastIndexOf('/');
        String type = file.getContentType().substring(lastIndex + 1);

        studentQuestionService.updateStudentQuestionImage(studentQuestionId, type);

        url = studentQuestionService.findStudentQuestionById(studentQuestionId).getImage().getUrl();
        Files.copy(file.getInputStream(), getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);

        return url;
    }

    @DeleteMapping("/studentQuestions/{studentQuestionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity removeStudentQuestion(@PathVariable Integer studentQuestionId) throws IOException {
        logger.debug("removeStudentQuestion studentQuestionId: {}: ", studentQuestionId);
        StudentQuestionDto studentQuestionDto = studentQuestionService.findStudentQuestionById(studentQuestionId);
        String url = studentQuestionDto.getImage() != null ? studentQuestionDto.getImage().getUrl() : null;

        studentQuestionService.removeStudentQuestion(studentQuestionId);

        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/studentQuestions/{studentQuestionId}/available")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#studentQuestionId, 'STUDENT_QUESTION.ACCESS')")
    public StudentQuestionDto makeStudentQuestionAvailable(@PathVariable Integer studentQuestionId) {
        return this.studentQuestionService.makeStudentQuestionAvailable(studentQuestionId);
    }
    
    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }

}