package ajudavcapi.domain.dto.stroke;

import java.time.LocalDate;

import ajudavcapi.domain.enums.StrokeType;

public record StrokeResponseDTO(

    Long id,
    StrokeType strokeType,
    LocalDate strokeDate

) {}