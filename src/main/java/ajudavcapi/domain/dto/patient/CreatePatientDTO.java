package ajudavcapi.domain.dto.patient;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ajudavcapi.domain.enums.StrokeType; // Ajuste para o pacote do seu Enum

public record CreatePatientDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name,

    @NotNull(message = "O tipo de AVC é obrigatório")
    StrokeType strokeType,

    @NotNull(message = "A data de nascimento é obrigatória")
    LocalDate birthDate,

    LocalDate strokeDate,

    String importantDescription
) {}