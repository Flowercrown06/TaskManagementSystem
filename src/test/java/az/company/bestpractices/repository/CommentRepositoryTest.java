package az.company.bestpractices.repository;

import az.company.bestpractices.entity.Comment;
import az.company.bestpractices.entity.Task;
import az.company.bestpractices.entity.User;
import az.company.bestpractices.enums.Role;
import az.company.bestpractices.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Task task;
    private User user;

    @BeforeEach
    void setUp() {
        // 1. Şərhi yazan User-i yaradırıq
        user = new User();
        user.setEmail("commenter@gmail.com");
        user.setFullName("Gultaj Commenter");
        user.setPassword("pass123");
        user.setRole(Role.USER);
        user.setDeleted(false);
        entityManager.persist(user);

        // 2. Şərhin aid olacağı Task-ı yaradırıq
        task = new Task();
        task.setTitle("Task for Comments");
        task.setStatus(TaskStatus.TO_DO);
        task.setUser(user);
        entityManager.persist(task);

        // 3. Şərh (Comment) yaradırıq
        Comment comment = new Comment();
        comment.setContent("This is a test comment.");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setTask(task);

        entityManager.persist(comment);
        entityManager.flush();
    }

    @Test
    void findAllByTaskId_WhenCommentsExist_ShouldReturnCommentList() {
        // Action
        List<Comment> comments = commentRepository.findAllByTaskId(task.getId());

        // Assertion
        assertThat(comments).isNotEmpty();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("This is a test comment.");
        assertThat(comments.get(0).getTask().getId()).isEqualTo(task.getId());
    }

    @Test
    void findAllByTaskId_WhenNoComments_ShouldReturnEmptyList() {
        // Action - Mövcud olmayan bir Task ID ilə yoxlayırıq
        List<Comment> comments = commentRepository.findAllByTaskId(999L);

        // Assertion
        assertThat(comments).isEmpty();
    }
}