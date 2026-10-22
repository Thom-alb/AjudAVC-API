
package ajudavcapi.domain.dto.group;


import io.swagger.v3.oas.annotations.media.Schema;
import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record CreateGroupDTO(

    @NotBlank(message = "O nome do grupo é obrigatório.")
    @Schema(description = "Nome do grupo de cuidado.")
    String name,

    @Valid
    @Schema(description = "Dados opcionais do paciente associados ao grupo.") CreatePatientDTO patient

) {
}
