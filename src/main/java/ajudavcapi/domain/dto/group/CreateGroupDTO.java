package ajudavcapi.domain.dto.group;

import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGroupDTO(
    @NotBlank(message = "O nome do grupo de cuidado é obrigatório.")
    @Size(min = 3, max = 80, message = "O nome do grupo deve ter entre 3 e 80 caracteres.")
    String name,

    @NotNull(message = "Os dados do paciente são obrigatórios.")
    @Valid // Valida recursivamente as anotações dentro de CreatePatientDTO
    CreatePatientDTO patient
) {}