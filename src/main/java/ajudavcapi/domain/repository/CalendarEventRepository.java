package ajudavcapi.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ajudavcapi.domain.entity.CalendarEventEntity;

public interface CalendarEventRepository extends JpaRepository<CalendarEventEntity, Long> {

    List<CalendarEventEntity> findByEventCategory(String eventCategory);

    List<CalendarEventEntity> findByGroupIdOrderByEventDateTimeAsc(Long groupId);

    List<CalendarEventEntity> findByGroupIdAndEventDateTimeBetweenOrderByEventDateTimeAsc(
            Long groupId, LocalDateTime start, LocalDateTime end);

    long countByGroupIdAndEventDateTimeGreaterThanEqualAndEventDateTimeLessThanEqual(
            Long groupId, LocalDateTime start, LocalDateTime end);

    long countByGroupIdAndEventDateTimeGreaterThanEqualAndEventDateTimeLessThanEqualAndCompletedTrue(
            Long groupId, LocalDateTime start, LocalDateTime end);
}
