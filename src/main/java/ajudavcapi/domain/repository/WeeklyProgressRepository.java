package ajudavcapi.domain.repository;

import ajudavcapi.domain.entity.WeeklyProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeeklyProgressRepository extends JpaRepository<WeeklyProgressEntity, Long> {

    @Query("SELECT w FROM WeeklyProgressEntity w " +
           "JOIN FETCH w.user " +
           "JOIN FETCH w.group " +
           "WHERE w.group.id = :groupId " +
           "ORDER BY w.createdAt DESC")
    List<WeeklyProgressEntity> findByGroupIdFetchUser(@Param("groupId") Long groupId);

    @Query("SELECT w FROM WeeklyProgressEntity w WHERE w.group.id = :groupId AND MONTH(w.createdAt) = :month AND YEAR(w.createdAt) = :year ORDER BY w.createdAt ASC")
    List<WeeklyProgressEntity> findByGroupIdAndMonthAndYear(@Param("groupId") Long groupId, @Param("month") Integer month, @Param("year") Integer year);
}
