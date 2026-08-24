package ajudavcapi.domain.dto.group;

import ajudavcapi.domain.entity.GroupEntity;

public record GroupResponseDTO(
    Long id,
    String name,
    String inviteCode,
    String patientName
) {
    public GroupResponseDTO(GroupEntity entity) {
        this(
            entity.getId(),
            entity.getName(),
            entity.getInviteCode(),
            entity.getPatient() != null ? entity.getPatient().getName() : null
        );
    }
}