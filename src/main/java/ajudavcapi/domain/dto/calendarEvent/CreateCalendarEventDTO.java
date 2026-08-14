package ajudavcapi.domain.dto.calendarEvent;


import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCalendarEventDTO(
    @NotBlank(message = "O título do evento é obrigatório")
    String title,

    String description,

    @NotNull(message = "A data e hora do evento são obrigatórias")
    @FutureOrPresent(message = "A data do evento não pode estar no passado")
    LocalDateTime eventDateTime,

    @NotNull(message = "A categoria do evento é obrigatório")
    String eventCategory
) {}
