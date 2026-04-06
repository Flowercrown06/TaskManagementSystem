package az.company.bestpractices.repository;

import az.company.bestpractices.entity.User;
import az.company.bestpractices.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Sürətli JPA testi üçün (yalnız DB hissəsini yükləyir)
@ActiveProfiles("test")
class UserRepositoryTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User activeUser;

    @BeforeEach
    void setUp() {
        // Hər testdən əvvəl bazaya bir nümunə istifadəçi qoyuruq
        activeUser = new User();
        activeUser.setEmail("gultaj@gmail.com");
        activeUser.setFullName("Gultaj M.");
        activeUser.setPassword("pass123");
        activeUser.setRole(Role.USER); // Entity-də Enum varsa mütləq set etməlisən
        activeUser.setDeleted(false);

        entityManager.persist(activeUser);
        entityManager.flush();
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Action
        Optional<User> found = userRepository.findByEmail("gultaj@gmail.com");

        // Assertion
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("gultaj@gmail.com");
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        // Action
        boolean exists = userRepository.existsByEmail("gultaj@gmail.com");

        // Assertion
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // Action
        boolean exists = userRepository.existsByEmail("nonexistent@gmail.com");

        // Assertion
        assertThat(exists).isFalse();
    }

    @Test
    void findAllByDeletedFalse_ShouldReturnOnlyNonDeletedUsers() {
        // Given - Bir dənə də silinmiş istifadəçi yaradırıq
        User deletedUser = new User();
        deletedUser.setEmail("deleted@gmail.com");
        deletedUser.setFullName("Deleted User");
        deletedUser.setPassword("pass123");
        deletedUser.setDeleted(true); // Bu silinmişdir
        entityManager.persist(deletedUser);
        entityManager.flush();

        // Action
        List<User> users = userRepository.findAllByDeletedFalse();

        // Assertion
        assertThat(users).hasSize(1); // Bazada 2 nəfər var, amma deleted=false olan 1 nəfərdir
        assertThat(users.get(0).getEmail()).isEqualTo("gultaj@gmail.com");
    }
}