package ajudavcapi.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.CalendarEvent;
import ajudavcapi.domain.entity.Group;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByGroupAndEventDateBetweenOrderByEventDateAsc(
        Group group, 
        LocalDateTime start, 
        LocalDateTime end
    );
}