package ajudavcapi.domain.dto.group;

import ajudavcapi.domain.dto.patient.PatientResponseDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.mapper.PatientMapper;

public record GroupResponseDTO(
    Long id,
    String name,
    String inviteCode,
    String patientName,
    Long patientId,
    PatientResponseDTO patient
) {
    public GroupResponseDTO(GroupEntity entity) {
        this(
            entity.getId(),
            entity.getName(),
            entity.getInviteCode(),
            entity.getPatient() != null ? entity.getPatient().getName() : null,
            entity.getPatient() != null ? entity.getPatient().getId() : null,
            PatientMapper.toResponse(entity.getPatient(), entity.getId())
        );
    }
}
