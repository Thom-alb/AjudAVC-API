package ajudavcapi.domain.dto.calendarEvent;


import java.time.LocalDateTime;


public record CalendarEventResponseDTO(
    Long id,
    String title,
    String description,
    LocalDateTime eventDateTime,
    String eventCategory,
    boolean completed,
    String createdByName,
    Long groupId
) {}