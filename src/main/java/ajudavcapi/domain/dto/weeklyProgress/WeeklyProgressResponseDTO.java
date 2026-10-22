
package ajudavcapi.domain.dto.weeklyProgress;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Registro de evolução semanal com pontuações e métricas de atividades.")
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
