package ajudavcapi.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ajudavcapi.domain.entity.CalendarEventEntity;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEventEntity, Long> {

    // Listar eventos por cateoria
    List<CalendarEventEntity> findByEventCategory(String eventCategory);

    // Lista eventos de um grupo específico ordenados pela data e hora
    List<CalendarEventEntity> findByGroupIdOrderByEventDateTimeAsc(Long groupId);

    // Lista eventos entre um intervalo de datas (para filtros semanais/mensais no app)
    List<CalendarEventEntity> findByGroupIdAndEventDateTimeBetweenOrderByEventDateTimeAsc(
            Long groupId, LocalDateTime start, LocalDateTime end);
}