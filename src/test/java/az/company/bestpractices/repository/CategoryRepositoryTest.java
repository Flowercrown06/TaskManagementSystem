package az.company.bestpractices.repository;

import az.company.bestpractices.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category category;

    @BeforeEach
    void setUp() {
        // Test üçün bir kateqoriya yaradırıq
        category = new Category();
        category.setName("Development");

        entityManager.persist(category);
        entityManager.flush();
    }

    @Test
    void existsByName_WhenCategoryExists_ShouldReturnTrue() {
        // Action
        boolean exists = categoryRepository.existsByName("Development");

        // Assertion
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_WhenCategoryDoesNotExist_ShouldReturnFalse() {
        // Action
        boolean exists = categoryRepository.existsByName("Non-Existent");

        // Assertion
        assertThat(exists).isFalse();
    }

    @Test
    void findByName_WhenCategoryExists_ShouldReturnCategory() {
        // Action
        Optional<Category> found = categoryRepository.findByName("Development");

        // Assertion
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Development");
    }

    @Test
    void findByName_WhenCategoryDoesNotExist_ShouldReturnEmpty() {
        // Action
        Optional<Category> found = categoryRepository.findByName("HR");

        // Assertion
        assertThat(found).isEmpty();
    }
}