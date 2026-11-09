package ajudavcapi.domain.repository;

import ajudavcapi.domain.entity.WeeklyProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WeeklyProgressRepository extends JpaRepository<WeeklyProgressEntity, Long> {

    @Query("SELECT w FROM WeeklyProgressEntity w " +
           "JOIN FETCH w.user " +
           "JOIN FETCH w.group " +
           "WHERE w.group.id = :groupId " +
           "ORDER BY w.year DESC, w.month DESC, w.weekOfMonth ASC")
    List<WeeklyProgressEntity> findByGroupIdFetchUser(@Param("groupId") Long groupId);

    @Query("SELECT w FROM WeeklyProgressEntity w WHERE w.group.id = :groupId AND w.month = :month AND w.year = :year ORDER BY w.weekOfMonth ASC")
    List<WeeklyProgressEntity> findByGroupIdAndMonthAndYear(@Param("groupId") Long groupId, @Param("month") Integer month, @Param("year") Integer year);

    Optional<WeeklyProgressEntity> findByGroupIdAndYearAndMonthAndWeekOfMonth(
        Long groupId, Integer year, Integer month, Integer weekOfMonth
    );
}