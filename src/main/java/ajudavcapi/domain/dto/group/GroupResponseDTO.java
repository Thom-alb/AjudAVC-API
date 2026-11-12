package ajudavcapi.domain.dto.group;

import ajudavcapi.domain.dto.patient.PatientResponseDTO;
import ajudavcapi.domain.entity.GroupEntity;

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
            entity.getPatient() != null
                ? entity.getPatient().getName()
                : null,
            entity.getPatient() != null
                ? entity.getPatient().getId()
                : null,
            entity.getPatient() != null
                ? new PatientResponseDTO(
                    entity.getPatient().getId(),
                    entity.getPatient().getName(),
                    entity.getPatient().getBirthDate(),
                    entity.getPatient().getImportantDescription(),
                    entity.getPatient().getStrokes().stream()
                        .map(stroke -> new ajudavcapi.domain.dto.stroke.StrokeResponseDTO(
                            stroke.getId(),
                            stroke.getStrokeType(),
                            stroke.getStrokeDate()
                        ))
                        .toList(),
                    entity.getPatient().getDiseases().stream()
                        .map(disease -> new ajudavcapi.domain.dto.disease.DiseaseResponseDTO(
                            disease.getId(),
                            disease.getType()
                        ))
                        .toList(),
                    entity.getId()
                )
                : null
        );
    }
}