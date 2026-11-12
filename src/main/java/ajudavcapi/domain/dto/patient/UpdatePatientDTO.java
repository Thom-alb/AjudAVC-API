
package ajudavcapi.domain.dto.patient;

import java.time.LocalDate;
import java.util.List;

import ajudavcapi.domain.dto.stroke.UpdateStrokeDTO;
import ajudavcapi.domain.enums.DiseaseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record UpdatePatientDTO(

    @NotBlank(message = "O nome do paciente é obrigatório.")
    String name,

    @NotNull(message = "A data de nascimento é obrigatória.")
    @PastOrPresent(message = "A data de nascimento não pode ser futura.")
    LocalDate birthDate,

    String importantDescription,

    @NotNull(message = "A lista de AVCs deve ser informada.")
    @Valid
    List<UpdateStrokeDTO> strokes,

    @NotNull(message = "A lista de doenças deve ser informada.")
    List<DiseaseType> diseases
    

) {}
