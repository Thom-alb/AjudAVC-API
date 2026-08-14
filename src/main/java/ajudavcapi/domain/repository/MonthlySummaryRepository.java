package ajudavcapi.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ajudavcapi.domain.entity.MonthlySummaryEntity;

@Repository
public interface MonthlySummaryRepository extends JpaRepository<MonthlySummaryEntity, Long> {

    // Busca todos os resumos de um grupo ordenados por ano e mês (mais recentes primeiro)
    List<MonthlySummaryEntity> findByGroupIdOrderByYearDescMonthDesc(Long groupId);

    // Busca resumo de um mês/ano específico no grupo (para evitar duplicatas)
    Optional<MonthlySummaryEntity> findByGroupIdAndMonthAndYear(Long groupId, Integer month, Integer year);
}