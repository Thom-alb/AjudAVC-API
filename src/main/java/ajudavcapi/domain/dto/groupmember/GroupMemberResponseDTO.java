
package ajudavcapi.domain.dto.groupmember;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import ajudavcapi.domain.enums.GroupRole;

@Schema(description = "Representação do membro e de suas permissões no grupo.")
public record GroupMemberResponseDTO(
    Long id,
    Long userId,
    String userName,
    String userEmail,
    GroupRole role,
    LocalDateTime joinedAt,
    boolean canEditRoutine,
    boolean canEditProgress
) {}
