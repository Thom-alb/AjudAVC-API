
package ajudavcapi.domain.dto.activitylog;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateActivityLogDTO(
    @NotBlank(message = "O título da atividade é obrigatório")
    @Schema(description = "Título curto da atividade ou registro do diário.")
    String title,

    @Schema(description = "Detalhes adicionais da atividade.")

    String description,

    @NotNull(message = "A data e hora da atividade são obrigatórias")
    @Schema(description = "Data e hora da atividade no formato ISO local.") LocalDateTime activityDate,

    @Schema(description = "Categoria da atividade, conforme os valores aceitos pela API.")

    String category, // Ex: "MEDICATION", "HYGIENE", "FEEDING", "THERAPY"

    @Schema(description = "Estado inicial da atividade, conforme os valores aceitos pela API.")

    String status    // PENDING (padrão), COMPLETED, CANCELED
) {}
