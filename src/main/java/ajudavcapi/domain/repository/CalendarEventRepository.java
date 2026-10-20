package ajudavcapi.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ajudavcapi.domain.entity.CalendarEventEntity;

public interface CalendarEventRepository extends JpaRepository<CalendarEventEntity, Long> {

    List<CalendarEventEntity> findByEventCategory(String eventCategory);

    List<CalendarEventEntity> findByGroupIdOrderByEventDateTimeAsc(Long groupId);

    List<CalendarEventEntity> findByGroupIdAndEventDateTimeBetweenOrderByEventDateTimeAsc(
            Long groupId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(c) FROM CalendarEventEntity c WHERE c.group.id = :groupId AND c.eventDateTime BETWEEN :start AND :end")
    long countByGroupIdAndEventDateTimeBetween(@Param("groupId") Long groupId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(c) FROM CalendarEventEntity c WHERE c.group.id = :groupId AND c.completed = true AND c.eventDateTime BETWEEN :start AND :end")
    long countCompletedByGroupIdAndEventDateTimeBetween(@Param("groupId") Long groupId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
