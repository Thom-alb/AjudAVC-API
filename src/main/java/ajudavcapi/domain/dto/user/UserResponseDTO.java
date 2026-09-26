package ajudavcapi.domain.dto.user;

import java.time.LocalDateTime;
import ajudavcapi.domain.entity.UserEntity;

public record UserResponseDTO(
    Long id,
    String name, 
    String email,
    String provider, // Adicionado para expor a origem do cadastro (Útil no React Native)
    LocalDateTime createdAt
) {
    
    // Construtor canônico adaptado para mapear a Entity diretamente
    public UserResponseDTO(UserEntity u) {
        this(
            u.getId(), 
            u.getName(), 
            u.getEmail(), 
            u.getProvider() != null ? u.getProvider() : "LOCAL", // Fallback seguro caso o campo seja nulo no banco
            u.getCreatedAt()
        );
    }

}
