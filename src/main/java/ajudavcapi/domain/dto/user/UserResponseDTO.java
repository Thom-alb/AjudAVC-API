package ajudavcapi.domain.dto.user;

import java.time.LocalDateTime;
import ajudavcapi.domain.entity.UserEntity;

public record UserResponseDTO(
    Long id,
    String name, 
    String email,
    String provider, 
    LocalDateTime createdAt
) {
    
    public UserResponseDTO(UserEntity u) {
        this(
            u.getId(), 
            u.getName(), 
            u.getEmail(), 
            u.getProvider() != null ? u.getProvider() : "LOCAL", 
            u.getCreatedAt()
        );
    }

}
