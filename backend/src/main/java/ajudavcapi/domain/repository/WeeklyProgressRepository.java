package ajudavcapi.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;

@Repository
public interface WeeklyProgressRepository extends JpaRepository<WeeklyProgressEntity, Long> {
    List<WeeklyProgressEntity> findByGroupOrderByCreatedAtDesc(GroupEntity group);
}