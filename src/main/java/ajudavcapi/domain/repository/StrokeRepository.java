package ajudavcapi.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ajudavcapi.domain.entity.StrokeEntity;

@Repository
public interface StrokeRepository extends JpaRepository<StrokeEntity, Long> {

    List<StrokeEntity> findByPatientId(Long patientId);

}