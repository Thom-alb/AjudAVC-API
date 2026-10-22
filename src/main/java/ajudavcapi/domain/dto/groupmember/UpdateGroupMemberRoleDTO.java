
package ajudavcapi.domain.dto.groupmember;



import io.swagger.v3.oas.annotations.media.Schema;
import ajudavcapi.domain.enums.GroupRole;
import jakarta.validation.constraints.NotNull;

public record UpdateGroupMemberRoleDTO(
    @NotNull(message = "A nova função é obrigatória")
    @Schema(description = "Papel do membro dentro do grupo, por exemplo LEADER ou MEMBER.")
    GroupRole role
) {}
