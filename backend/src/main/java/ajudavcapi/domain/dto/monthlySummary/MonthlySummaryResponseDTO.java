package ajudavcapi.domain.dto.monthlySummary;

import java.time.LocalDateTime;

public record MonthlySummaryResponseDTO(
    Long id,
    Integer month,
    Integer year,
    Double averageCommunication,
    Double averageMobility,
    Double averageMemory,
    Integer countAnimo,
    Integer countFeliz,
    Integer countApatia,
    Integer countRaiva,
    Integer countTriste,
    LocalDateTime createdAt,
    Long groupId
) {}
