package ajudavcapi.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.ActivityLogEntity;
import ajudavcapi.domain.entity.GroupEntity;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {
    List<ActivityLogEntity> findByGroupOrderByActivityDateDesc(GroupEntity group);
    Optional<ActivityLogEntity> findByTitle(String title);
    Optional<ActivityLogEntity> findByTitleIgnoreCase(String title);

}