
package ajudavcapi.domain.dto.weeklyProgress;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateWeeklyProgressDTO(
    @NotNull(message = "A nota de comunicação é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 10, message = "A nota máxima é 10")
    @Schema(description = "Pontuação de comunicação de 1 a 10.")
    Integer communicationScore,

    @NotNull(message = "A nota de mobilidade é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 10, message = "A nota máxima é 10")
    @Schema(description = "Pontuação de mobilidade de 1 a 10.")
    Integer mobilityScore,

    @NotNull(message = "A nota de memória é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 10, message = "A nota máxima é 10")
    @Schema(description = "Pontuação de memória de 1 a 10.")
    Integer memoryScore,

    @Schema(description = "Contagem/nota registrada para o humor ânimo, conforme regra de negócio.")

    Integer countAnimo,
    @Schema(description = "Contagem/nota registrada para o humor feliz.")
    Integer countFeliz,
    @Schema(description = "Contagem/nota registrada para o humor apatia.")
    Integer countApatia,
    @Schema(description = "Contagem/nota registrada para o humor raiva.")
    Integer countRaiva,
    @Schema(description = "Contagem/nota registrada para o humor triste.")
    Integer countTriste,

    @Schema(description = "Observações sobre a evolução semanal.")

    String description
) {}
