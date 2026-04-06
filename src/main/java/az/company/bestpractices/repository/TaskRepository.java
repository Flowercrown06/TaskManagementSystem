package az.company.bestpractices.repository;

import az.company.bestpractices.entity.Task;
import az.company.bestpractices.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByStatus(TaskStatus status);

    List<Task> findAllByUserId(Long userId);

    boolean existsByTitle(String title);

    List<Task> findAllByUserEmail(String email);

    // Vaxtı keçmiş və hələ bitməmiş tasklar
    List<Task> findAllByStatusNotAndDeadlineBefore(TaskStatus status, LocalDateTime deadline);

    // Deadline-na tam olaraq 1 saat qalan tasklar
    @Query("SELECT t FROM Task t WHERE t.status != 'DONE' AND t.deadline BETWEEN :start AND :end")
    List<Task> findTasksDueInAnHour(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}