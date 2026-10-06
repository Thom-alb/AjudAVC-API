package ajudavcapi.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ajudavcapi.domain.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}