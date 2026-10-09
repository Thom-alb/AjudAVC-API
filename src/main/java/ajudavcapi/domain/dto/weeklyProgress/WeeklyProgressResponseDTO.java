package ajudavcapi.domain.dto.weeklyProgress;

import java.time.LocalDateTime;

public record WeeklyProgressResponseDTO(
    Long id,
    Integer communicationScore,
    Integer mobilityScore,
    Integer memoryScore,
    Integer countAnimo,
    Integer countFeliz,
    Integer countApatia,
    Integer countRaiva,
    Integer countTriste,
    String description,
    Integer weekOfMonth,
    LocalDateTime createdAt,
    String authorName,
    Long groupId,
    Integer plannedActivities,
    Integer completedActivities
) {}
