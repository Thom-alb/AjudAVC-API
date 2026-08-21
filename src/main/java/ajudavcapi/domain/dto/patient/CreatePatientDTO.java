package ajudavcapi.domain.dto.patient;

import java.time.LocalDate;

import ajudavcapi.domain.enums.StrokeType; 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record CreatePatientDTO(
    @NotBlank(message = "O nome do paciente é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome do paciente deve ter entre 2 e 100 caracteres.")
    String name,

    @NotNull(message = "O tipo de AVC é obrigatório.")
    StrokeType strokeType,

    @PastOrPresent(message = "A data do AVC não pode ser uma data futura.")
    LocalDate strokeDate,

    @PastOrPresent(message = "A data de nascimento não pode ser uma data futura.")
    LocalDate birthDate,

    @Size(max = 500, message = "A descrição importante deve ter no máximo 500 caracteres.")
    String importantDescription
) {}