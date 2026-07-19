package ajudavcapi.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.Group;
import ajudavcapi.domain.entity.WeeklyProgress;

@Repository
public interface WeeklyProgressRepository extends JpaRepository<WeeklyProgress, Long> {
    List<WeeklyProgress> findByGroupOrderByCreatedAtDesc(Group group);
}