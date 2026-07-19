package ajudavcapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}