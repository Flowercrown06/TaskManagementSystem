package az.gultaj.taskmanagement.repository;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @MockBean
    private JavaMailSender javaMailSender; // Mail xətasının qarşısını almaq üçün

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        // Task yarada bilmək üçün əvvəlcə bir User yaradıb bazaya yazmalıyıq (Foreign Key üçün)
        user = new User();
        user.setEmail("gultaj.task@gmail.com");
        user.setFullName("Gultaj Tester");
        user.setPassword("12345");
        user.setRole(Role.USER);
        user.setDeleted(false);

        entityManager.persist(user);

        // Bir dənə də Task yaradaq
        Task task = new Task();
        task.setTitle("Complete Unit Tests");
        task.setDescription("Writing repository tests for Task management");
        task.setStatus(TaskStatus.TO_DO);
        task.setUser(user); // User-i set edirik

        entityManager.persist(task);
        entityManager.flush();
    }

    @Test
    void findAllByStatus_ShouldReturnTasksWithSpecificStatus() {
        // Action
        List<Task> tasks = taskRepository.findAllByStatus(TaskStatus.TO_DO);

        // Assertion
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getStatus()).isEqualTo(TaskStatus.TO_DO);
    }

    @Test
    void existsByTitle_WhenTitleExists_ShouldReturnTrue() {
        // Action
        boolean exists = taskRepository.existsByTitle("Complete Unit Tests");

        // Assertion
        assertThat(exists).isTrue();
    }

    @Test
    void findAllByUserEmail_WhenUserHasTasks_ShouldReturnTaskList() {
        // Action
        List<Task> tasks = taskRepository.findAllByUserEmail("gultaj.task@gmail.com");

        // Assertion
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getUser().getEmail()).isEqualTo("gultaj.task@gmail.com");
    }

    @Test
    void findAllByUserId_ShouldReturnTasksForSpecificUser() {
        // Action
        List<Task> tasks = taskRepository.findAllByUserId(user.getId());

        // Assertion
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getUser().getId()).isEqualTo(user.getId());
    }
}