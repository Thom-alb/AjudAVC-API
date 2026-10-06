package ajudavcapi.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ajudavcapi.domain.entity.ActivityLogEntity;

public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {

    List<ActivityLogEntity> findByGroupIdOrderByActivityDateDesc(Long groupId);
    Optional<ActivityLogEntity> findByTitle(String title);
    Optional<ActivityLogEntity> findByTitleIgnoreCase(String title);

}