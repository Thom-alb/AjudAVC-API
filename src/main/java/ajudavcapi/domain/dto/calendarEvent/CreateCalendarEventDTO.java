
package ajudavcapi.domain.dto.calendarEvent;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCalendarEventDTO(
    @NotBlank(message = "O título do evento é obrigatório")
    @Schema(description = "Título do evento da agenda.")
    String title,
    @Schema(description = "Detalhes do evento ou instruções de cuidado.")
    String description,
    @NotNull(message = "A data e hora do evento são obrigatórias")
    @FutureOrPresent(message = "A data do evento não pode estar no passado")
    @Schema(description = "Data e hora local do evento, no formato ISO yyyy-MM-ddTHH:mm:ss.") LocalDateTime eventDateTime,
    @NotNull(message = "A categoria do evento é obrigatória")
    @Schema(description = "Categoria do evento, conforme os valores aceitos pela API.")
    String eventCategory,
    @Schema(description = "Se true, atribui o evento ao criador; false ou null deixa sem responsável.")
    Boolean assignToCreator
) {}
