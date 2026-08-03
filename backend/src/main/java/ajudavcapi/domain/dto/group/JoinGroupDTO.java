package ajudavcapi.domain.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinGroupDTO(
    @NotBlank(message = "O código de convite é obrigatório")
    @Size(min = 6, max = 6, message = "O código de convite deve ter exatamente 6 caracteres")
    String inviteCode
) {}