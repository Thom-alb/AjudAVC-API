package ajudavcapi.domain.dto.groupmember;

import jakarta.validation.constraints.NotNull;

public record UpdateGroupMemberPermissionsDTO(
    @NotNull(message = "A permissão de editar rotina é obrigatória")
    Boolean canEditRoutine,
    @NotNull(message = "A permissão de editar progresso é obrigatória")
    Boolean canEditProgress
) {}
