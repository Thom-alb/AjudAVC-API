package ajudavcapi.domain.dto.groupmember;

import java.time.LocalDateTime;
import ajudavcapi.domain.enums.GroupRole;

public record GroupMemberResponseDTO(
    Long id,
    Long userId,
    String userName,
    String userEmail,
    GroupRole role,
    LocalDateTime joinedAt
) {}