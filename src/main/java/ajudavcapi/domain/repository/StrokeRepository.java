package ajudavcapi.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ajudavcapi.domain.entity.StrokeEntity;


public interface StrokeRepository extends JpaRepository<StrokeEntity, Long> {

    List<StrokeEntity> findByPatientId(Long patientId);

}