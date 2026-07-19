package ajudavcapi.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.DeviceToken;
import ajudavcapi.domain.entity.User;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findByUser(User user);
    Optional<DeviceToken> findByToken(String token);
}