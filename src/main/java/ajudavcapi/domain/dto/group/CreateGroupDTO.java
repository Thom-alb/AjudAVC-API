package ajudavcapi.domain.dto.group;

import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record CreateGroupDTO(

    @NotBlank(message = "O nome do grupo é obrigatório.")
    String name,

    @Valid
    CreatePatientDTO patient

) {
}