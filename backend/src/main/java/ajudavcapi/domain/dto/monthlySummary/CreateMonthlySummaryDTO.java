package ajudavcapi.domain.dto.monthlySummary;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateMonthlySummaryDTO(
    @NotNull(message = "O mês é obrigatório")
    @Min(value = 1, message = "O mês deve ser entre 1 e 12")
    @Max(value = 12, message = "O mês deve ser entre 1 e 12")
    Integer month,

    @NotNull(message = "O ano é obrigatório")
    @Min(value = 2020, message = "Ano inválido")
    Integer year,

    Double averageCommunication,
    Double averageMobility,
    Double averageMemory,

    Integer countAnimo,
    Integer countFeliz,
    Integer countApatia,
    Integer countRaiva,
    Integer countTriste
) {}