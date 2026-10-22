
package ajudavcapi.domain.dto.groupmember;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateGroupMemberPermissionsDTO(
    @NotNull(message = "A permissão de editar rotina é obrigatória")
    @Schema(description = "Autoriza o membro a editar a rotina do grupo.")
    Boolean canEditRoutine,
    @NotNull(message = "A permissão de editar progresso é obrigatória")
    @Schema(description = "Autoriza o membro a editar registros de progresso.")
    Boolean canEditProgress
) {}
