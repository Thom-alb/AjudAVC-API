package ajudavcapi.domain.dto.groupmember;


import ajudavcapi.domain.enums.GroupRole;
import jakarta.validation.constraints.NotNull;

public record UpdateGroupMemberRoleDTO(
    @NotNull(message = "A nova função é obrigatória")
    GroupRole role
) {}