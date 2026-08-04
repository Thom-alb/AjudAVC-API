package ajudavcapi.domain.dto.activitylog;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateActivityLogDTO(
    @NotBlank(message = "O título da atividade é obrigatório")
    String title,

    String description,

    @NotNull(message = "A data e hora da atividade são obrigatórias")
    LocalDateTime activityDate,

    String category, // Ex: "MEDICATION", "HYGIENE", "FEEDING", "THERAPY"

    String status    // PENDING (padrão), COMPLETED, CANCELED
) {}
