
package ajudavcapi.domain.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
  
    @NotBlank(message = "O nome é obrigatório")
    @Schema(description = "Nome completo do usuário.")
    String name,

    @Email (message = "E-mail inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    @Schema(description = "E-mail válido e único da conta.")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
     @Schema(description = "Senha da conta com no mínimo 6 caracteres.")
     String password
) {
    
}
