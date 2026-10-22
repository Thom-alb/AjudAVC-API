
package ajudavcapi.domain.dto.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
    @NotBlank @Schema(description = "E-mail cadastrado usado para autenticação.") String email,
    @NotBlank @Schema(description = "Senha da conta. Nunca compartilhe este valor.") String password
) {}
