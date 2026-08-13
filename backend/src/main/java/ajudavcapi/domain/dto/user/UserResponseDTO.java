package ajudavcapi.domain.dto.user;

import java.time.LocalDateTime;

import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.AuthProvider;
public record UserResponseDTO(
    Long id,
    String name, 
    String email,
    LocalDateTime createdAt,
    AuthProvider authProvider
) {
    
    public UserResponseDTO(UserEntity u) {
        this(u.getId(), u.getName(), u.getEmail(), u.getCreatedAt(), u.getAuthProvider());
    }

}
