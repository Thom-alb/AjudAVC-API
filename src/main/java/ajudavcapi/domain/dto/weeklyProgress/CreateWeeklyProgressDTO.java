package ajudavcapi.domain.dto.weeklyProgress;

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

    Integer countAnimo,
    Integer countFeliz,
    Integer countApatia,
    Integer countRaiva,
    Integer countTriste,

    String description
) {}
