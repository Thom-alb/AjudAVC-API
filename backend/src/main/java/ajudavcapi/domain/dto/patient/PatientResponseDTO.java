package ajudavcapi.domain.dto.patient;

import java.time.LocalDate;

public record PatientResponseDTO(
    Long id,
    String name,
    LocalDate birthDate,
    LocalDate strokeDate,
    Long groupId
) {} 
    
