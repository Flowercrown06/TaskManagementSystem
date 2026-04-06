package az.gultaj.taskmanagement.repository;

import az.gultaj.taskmanagement.entity.Review;
import az.gultaj.taskmanagement.entity.Task;
import az.gultaj.taskmanagement.entity.User;
import az.gultaj.taskmanagement.enums.Role;
import az.gultaj.taskmanagement.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User reviewer;
    private Task task;

    @BeforeEach
    void setUp() {
        // 1. Reviewer (User) yaradırıq
        reviewer = new User();
        reviewer.setEmail("reviewer@gmail.com");
        reviewer.setFullName("Reviewer Gultaj");
        reviewer.setPassword("securePass");
        reviewer.setRole(Role.ADMIN);
        reviewer.setDeleted(false);
        entityManager.persist(reviewer);

        // 2. Task yaradırıq (Review bu task-a aid olacaq)
        task = new Task();
        task.setTitle("Test Task for Review");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setUser(reviewer); // Task-ı kiməsə bağlamalıyıq
        entityManager.persist(task);

        // 3. Review (Rəy) yaradırıq
        Review review = new Review();
        review.setFeedback("Excellent work!");
        review.setApproved(true);
        review.setReviewedAt(LocalDateTime.now());
        review.setTask(task);
        review.setReviewer(reviewer);

        entityManager.persist(review);
        entityManager.flush();
    }

    @Test
    void findByTaskId_WhenReviewExists_ShouldReturnReview() {
        // Action
        Optional<Review> found = reviewRepository.findByTaskId(task.getId());

        // Assertion
        assertThat(found).isPresent();
        assertThat(found.get().getFeedback()).isEqualTo("Excellent work!");
        assertThat(found.get().getTask().getId()).isEqualTo(task.getId());
    }

    @Test
    void findByReviewerEmail_WhenReviewerExists_ShouldReturnReview() {
        // Action
        Optional<Review> found = reviewRepository.findByReviewerEmail("reviewer@gmail.com");

        // Assertion
        assertThat(found).isPresent();
        assertThat(found.get().getReviewer().getEmail()).isEqualTo("reviewer@gmail.com");
    }

    @Test
    void existsByTaskId_WhenTaskHasReview_ShouldReturnTrue() {
        // Action
        boolean exists = reviewRepository.existsByTaskId(task.getId());

        // Assertion
        assertThat(exists).isTrue();
    }

    @Test
    void existsByTaskId_WhenTaskHasNoReview_ShouldReturnFalse() {
        // Action - Mövcud olmayan bir ID ilə yoxlayırıq
        boolean exists = reviewRepository.existsByTaskId(999L);

        // Assertion
        assertThat(exists).isFalse();
    }
}