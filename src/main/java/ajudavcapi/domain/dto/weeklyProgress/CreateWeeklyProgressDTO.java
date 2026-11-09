package ajudavcapi.domain.dto.weeklyProgress;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateWeeklyProgressDTO(
    @NotNull(message = "O mês é obrigatório")
    @Min(value = 1, message = "O mês deve ser entre 1 e 12")
    @Max(value = 12, message = "O mês deve ser entre 1 e 12")
    Integer month,

    @NotNull(message = "O ano é obrigatório")
    Integer year,

    @NotNull(message = "A semana do mês é obrigatória")
    @Min(value = 1, message = "A semana mínima é 1")
    @Max(value = 4, message = "A semana máxima é 4")
    @Schema(description = "Semana do mês de 1 a 4.")
    Integer weekOfMonth,

    @NotNull(message = "A nota de comunicação é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 10, message = "A nota máxima é 10")
    Integer communicationScore,

    @NotNull(message = "A nota de mobilidade é obrigatória")
    @Min(value = 1) @Max(10)
    Integer mobilityScore,

    @NotNull(message = "A nota de memória é obrigatória")
    @Min(value = 1) @Max(10)
    Integer memoryScore,

    Integer countAnimo,
    Integer countFeliz,
    Integer countApatia,
    Integer countRaiva,
    Integer countTriste,

    String description
) {}