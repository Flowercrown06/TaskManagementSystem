package az.company.bestpractices.repository;

import az.company.bestpractices.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

     Optional<Review> findByTaskId(Long taskId);

     Optional<Review> findByReviewerEmail(String email);

     boolean existsByTaskId(Long taskId);
}