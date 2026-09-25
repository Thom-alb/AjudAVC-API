package ajudavcapi.domain.dto.patient;

import java.time.LocalDate;
import java.util.List;

import ajudavcapi.domain.dto.stroke.CreateStrokeDTO;
import ajudavcapi.domain.enums.DiseaseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record UpdatePatientDTO(
    @NotBlank(message = "O nome do paciente é obrigatório")
    String name,

    @NotNull(message = "A data de nascimento é obrigatória")
    @PastOrPresent(message = "A data de nascimento não pode ser uma data futura")
    LocalDate birthDate,

    String importantDescription,

    @Valid
    List<CreateStrokeDTO> strokes,

    List<DiseaseType> diseases
) {
}