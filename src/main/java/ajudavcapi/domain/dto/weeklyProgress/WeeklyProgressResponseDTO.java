package ajudavcapi.domain.dto.weeklyProgress;

import java.time.LocalDateTime;
import ajudavcapi.domain.enums.MoodState;

public record WeeklyProgressResponseDTO(
    Long id,
    Integer communicationScore,
    Integer mobilityScore,
    Integer memoryScore,
    MoodState moodState,
    String description,
    LocalDateTime createdAt,
    String authorName,
    Long groupId
) {}