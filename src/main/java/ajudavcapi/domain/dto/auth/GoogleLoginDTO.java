
package ajudavcapi.domain.dto.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GoogleLoginDTO(@NotBlank @Schema(description = "ID token recebido do SDK do Google; será validado pela API.") String idToken) {}
