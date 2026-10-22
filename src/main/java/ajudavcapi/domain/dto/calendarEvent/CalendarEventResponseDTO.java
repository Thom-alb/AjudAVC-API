package ajudavcapi.domain.dto.calendarEvent;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Representação de evento da agenda, incluindo estado e responsável.")
public record CalendarEventResponseDTO(
    Long id,
    String title,
    String description,
    LocalDateTime eventDateTime,
    String eventCategory,
    boolean completed,
    String createdByName,
    Long groupId,
    Long assignedUserId,
    String assignedUserName
) {}