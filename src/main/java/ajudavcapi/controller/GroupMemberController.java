
package ajudavcapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ajudavcapi.domain.dto.groupmember.GroupMemberResponseDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberRoleDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberPermissionsDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.GroupMemberService;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Membros da Rede", description = "Gestão de membros, papéis e permissões customizadas do grupo.")
@RequestMapping("/group-members")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    // Injeção via Construtor explícita
    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    @Operation(summary = "Listar membros da rede", description = "Lista os membros do grupo do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GroupMemberResponseDTO>> getGroupMembers(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<GroupMemberResponseDTO> members = groupMemberService.getGroupMembers(userLogado);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Atualizar papel do membro", description = "Altera o papel do membro. Somente o líder real do grupo deve poder executar essa ação; a regra é validada no serviço.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PutMapping("/{id}/role")
    public ResponseEntity<GroupMemberResponseDTO> updateMemberRole(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para UpdateGroupMemberRoleDTO", required = true) @RequestBody @Valid UpdateGroupMemberRoleDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupMemberResponseDTO response = groupMemberService.updateMemberRole(id, dto, userLogado);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar permissões customizadas", description = "Atualiza canEditRoutine e canEditProgress. Somente o líder do grupo deve conceder ou revogar permissões.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PutMapping("/{id}/permissions")
    public ResponseEntity<GroupMemberResponseDTO> updateMemberPermissions(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para UpdateGroupMemberPermissionsDTO", required = true) @RequestBody @Valid UpdateGroupMemberPermissionsDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {
        return ResponseEntity.ok(groupMemberService.updateMemberPermissions(id, dto, userLogado));
    }

    @Operation(summary = "Remover membro", description = "Remove membro do grupo. A operação deve ser restrita ao líder real do grupo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso excluído sem corpo de resposta"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Exclusão conflita com regra de negócio")
        })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMember(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        groupMemberService.removeMember(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}
