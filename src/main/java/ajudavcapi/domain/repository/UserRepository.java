package ajudavcapi.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

}