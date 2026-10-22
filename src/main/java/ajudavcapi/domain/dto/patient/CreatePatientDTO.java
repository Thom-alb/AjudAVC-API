
package ajudavcapi.domain.dto.patient;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

import ajudavcapi.domain.dto.stroke.CreateStrokeDTO;
import ajudavcapi.domain.enums.DiseaseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record CreatePatientDTO(
    @NotBlank(message = "O nome do paciente é obrigatório")
    @Schema(description = "Nome completo do paciente.")
    String name,

    @NotNull(message = "A data de nascimento é obrigatória")
    @PastOrPresent(message = "A data de nascimento não pode ser uma data futura")
    @Schema(description = "Data de nascimento no formato ISO yyyy-MM-dd.")
    LocalDate birthDate,

    @Schema(description = "Observações importantes para o cuidado.")

    String importantDescription,

    @Valid
    @Schema(description = "Lista de eventos AVC/AIT informados para o paciente.")
    List<CreateStrokeDTO> strokes,

    @Schema(description = "Doenças e condições de saúde selecionadas.")

    List<DiseaseType> diseases
) {
}
