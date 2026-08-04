package ajudavcapi.domain.dto.patient;

import java.time.LocalDate;
import ajudavcapi.domain.enums.StrokeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

public record CreatePatientDTO(
    @NotBlank(message = "O nome do paciente é obrigatório")
    String name,

    @NotBlank(message = "O tipo de AVC do paciente é obrigatório")
    StrokeType strokeType,

    @PastOrPresent(message = "A data de nascimento não pode ser no futuro")
    LocalDate birthDate,

    @PastOrPresent(message = "A data do AVC não pode ser no futuro")
    LocalDate strokeDate
) {}
