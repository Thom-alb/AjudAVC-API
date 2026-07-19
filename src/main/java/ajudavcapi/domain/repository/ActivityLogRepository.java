package ajudavcapi.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.ActivityLog;
import ajudavcapi.domain.entity.Group;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByGroupOrderByActivityDateDesc(Group group);
}