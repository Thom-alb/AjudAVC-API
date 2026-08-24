package ajudavcapi.domain.dto.weeklyProgress;

import ajudavcapi.domain.enums.MoodState;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateWeeklyProgressDTO(
    @NotNull(message = "A nota de comunicação é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 10, message = "A nota máxima é 10")
    Integer communicationScore,

    @NotNull(message = "A nota de mobilidade é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 10, message = "A nota máxima é 10")
    Integer mobilityScore,

    @NotNull(message = "A nota de memória é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 10, message = "A nota máxima é 10")
    Integer memoryScore,

    @NotNull(message = "O estado de humor é obrigatório")
    MoodState moodState,

    String description
) {}
