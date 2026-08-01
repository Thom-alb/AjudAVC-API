package ajudavcapi.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.MonthlySummaryEntity;

@Repository
public interface MonthlySummaryRepository extends JpaRepository<MonthlySummaryEntity, Long> {
    Optional<MonthlySummaryEntity> findByGroupAndMonthAndYear(GroupEntity group, Integer month, Integer year);
}