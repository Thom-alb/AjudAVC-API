
package ajudavcapi.domain.dto.auth;


import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Token JWT emitido pela API após autenticação.")
public record TokenResponseDTO(String token) {}
