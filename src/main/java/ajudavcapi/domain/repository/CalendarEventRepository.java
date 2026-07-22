package ajudavcapi.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.CalendarEventEntity;
import ajudavcapi.domain.entity.GroupEntity;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEventEntity, Long> {
    List<CalendarEventEntity> findByGroupAndEventDateBetweenOrderByEventDateAsc(
        GroupEntity group, 
        LocalDateTime start, 
        LocalDateTime end
    );
}