package ajudavcapi.domain.dto.patient;

import java.time.LocalDate;
import ajudavcapi.domain.enums.StrokeType; // Ajuste para o pacote do seu Enum

public record PatientResponseDTO(
    Long id,
    String name,
    StrokeType strokeType,
    LocalDate birthDate,
    LocalDate strokeDate,
    String importantDescription,
    Long groupId
) {}