package ajudavcapi.domain.dto.group;

import ajudavcapi.domain.dto.patient.CreatePatientDTO; // ou um UpdatePatientDTO caso possua
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record UpdateGroupDTO(
    @NotBlank(message = "O nome do grupo é obrigatório.")
    String name,

    @Valid
    CreatePatientDTO patient
) {
}