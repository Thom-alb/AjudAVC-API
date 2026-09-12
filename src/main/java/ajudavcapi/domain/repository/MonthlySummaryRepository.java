package ajudavcapi.domain.repository;

import ajudavcapi.domain.entity.MonthlySummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MonthlySummaryRepository extends JpaRepository<MonthlySummaryEntity, Long> {

    @Query("SELECT m FROM MonthlySummaryEntity m WHERE m.group.id = :groupId AND m.month = :month AND m.year = :year")
    Optional<MonthlySummaryEntity> findByGroupIdAndMonthAndYear(
            @Param("groupId") Long groupId, 
            @Param("month") Integer month, 
            @Param("year") Integer year
    );

    @Query("SELECT m FROM MonthlySummaryEntity m WHERE m.group.id = :groupId ORDER BY m.year DESC, m.month DESC")
    List<MonthlySummaryEntity> findByGroupIdOrderByYearDescMonthDesc(@Param("groupId") Long groupId);
}