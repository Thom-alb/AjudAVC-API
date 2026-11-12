
package ajudavcapi.domain.dto.group;

import jakarta.validation.constraints.NotBlank;

public record UpdateGroupDTO(

    @NotBlank(message = "O nome do grupo é obrigatório.")
    String name

) {}
