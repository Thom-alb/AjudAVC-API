package ajudavcapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ajudavcapi.domain.entity.DiseaseEntity;

@Repository
public interface DiseaseRepository extends JpaRepository<DiseaseEntity, Long> {

}