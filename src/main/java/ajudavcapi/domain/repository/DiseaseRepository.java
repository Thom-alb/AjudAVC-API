package ajudavcapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import ajudavcapi.domain.entity.DiseaseEntity;


public interface DiseaseRepository extends JpaRepository<DiseaseEntity, Long> {

}