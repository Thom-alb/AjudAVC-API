package ajudavcapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ajudavcapi.domain.entity.PatientEntity;


public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
}