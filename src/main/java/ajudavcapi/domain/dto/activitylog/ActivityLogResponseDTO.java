package ajudavcapi.domain.dto.activitylog;

import java.time.LocalDateTime;

public record ActivityLogResponseDTO(
    Long id,
    String title,
    String description,
    LocalDateTime activityDate,
    String status,
    String category,
    LocalDateTime createdAt,
    String authorName,
    Long groupId
) {}