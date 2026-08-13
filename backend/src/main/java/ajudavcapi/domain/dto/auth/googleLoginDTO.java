package ajudavcapi.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginDTO(
    @NotBlank String idToken
) {}