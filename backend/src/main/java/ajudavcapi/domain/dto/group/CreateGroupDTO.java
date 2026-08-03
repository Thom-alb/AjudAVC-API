package ajudavcapi.domain.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGroupDTO(
    @NotBlank(message = "O nome do grupo é obrigatório")
    @Size(min = 3, max = 100, message = "O nome do grupo deve ter entre 3 e 100 caracteres")
    String name
) {}