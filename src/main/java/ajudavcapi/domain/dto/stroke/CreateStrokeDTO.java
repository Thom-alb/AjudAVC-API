package ajudavcapi.domain.dto.stroke;

import java.time.LocalDate;

import ajudavcapi.domain.enums.StrokeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record CreateStrokeDTO(

    @NotNull(message = "O tipo de AVC é obrigatório.")
    StrokeType strokeType,

    @NotNull(message = "A data do AVC é obrigatória.")
    @PastOrPresent(message = "A data do AVC não pode ser uma data futura.")
    LocalDate strokeDate

) {}