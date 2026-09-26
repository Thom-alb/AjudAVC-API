package ajudavcapi.domain.dto.disease;

import ajudavcapi.domain.enums.DiseaseType;

public record DiseaseResponseDTO(

    Long id,
    DiseaseType type

) {}