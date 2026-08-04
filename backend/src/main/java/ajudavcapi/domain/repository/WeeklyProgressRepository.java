package ajudavcapi.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ajudavcapi.domain.entity.WeeklyProgressEntity;

@Repository
public interface WeeklyProgressRepository extends JpaRepository<WeeklyProgressEntity, Long> {

    // Traz o histórico de avaliações semanais do grupo, do mais recente para o mais antigo
    List<WeeklyProgressEntity> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}