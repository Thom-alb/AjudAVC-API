package ajudavcapi.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.Group;
import ajudavcapi.domain.entity.MonthlySummary;

@Repository
public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long> {
    Optional<MonthlySummary> findByGroupAndMonthAndYear(Group group, Integer month, Integer year);
}