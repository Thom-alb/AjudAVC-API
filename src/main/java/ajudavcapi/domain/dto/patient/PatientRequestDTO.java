package ajudavcapi.domain.dto.patient;


import java.time.LocalDate;
import ajudavcapi.domain.enums.StrokeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record PatientRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name,

    @NotNull(message = "O tipo de AVC é obrigatório")
    StrokeType strokeType, // Ou String, dependendo da sua modelagem

    LocalDate strokeDate,
    LocalDate birthDate,
    String importantDescription
) {}