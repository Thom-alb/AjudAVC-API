package ajudavcapi.domain.dto.group;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupDTO(
    @NotBlank(message = "O nome do grupo é obrigatório")
    String name,

    @NotBlank(message = "O nome do paciente é obrigatório")
    String patientName
    // Adicione outros campos do paciente aqui se houver (ex: Integer patientAge)
) {}