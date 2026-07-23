package ajudavcapi.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.DeviceToken;
import ajudavcapi.domain.entity.UserEntity;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    // Busca todos os dispositivos registrados para um usuário específico
    List<DeviceToken> findByUser(UserEntity user);

    // Busca um token específico para verificar se ele já está cadastrado no banco
    Optional<DeviceToken> findByToken(String token);

    // Exclui um token quando o usuário faz logout no celular
    void deleteByToken(String token);
}