package ajudavcapi.backend.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.backend.domain.entity.GroupEntity;
import ajudavcapi.backend.domain.entity.WeeklyProgressEntity;

@Repository
public interface WeeklyProgressRepository extends JpaRepository<WeeklyProgressEntity, Long> {
    List<WeeklyProgressEntity> findByGroupOrderByCreatedAtDesc(GroupEntity group);
}