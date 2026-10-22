
package ajudavcapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.group.CreateGroupDTO;
import ajudavcapi.domain.dto.group.GroupResponseDTO;
import ajudavcapi.domain.dto.group.JoinGroupDTO;
import ajudavcapi.domain.dto.group.UpdateGroupDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.GroupService;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Grupos de Cuidado", description = "Criação, consulta, atualização e ingresso em grupos multi-tenant de cuidado.")
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Criar um novo Grupo de Cuidado.
     */
    @Operation(summary = "Criar grupo de cuidado", description = "Cria um grupo de cuidado associado ao usuário autenticado, que passa a ser seu líder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso; confira o cabeçalho Location"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Grupo ou recurso relacionado não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito, como e-mail ou associação duplicada")
        })
    @PostMapping
    public ResponseEntity<GroupResponseDTO> createGroup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para CreateGroupDTO", required = true) @RequestBody @Valid CreateGroupDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        GroupResponseDTO response = groupService.createGroup(dto, userLogado);

        URI uri = uriBuilder.path("/groups/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    /**
     * Atualizar dados do Grupo (e do Paciente associado).
     */
    @Operation(summary = "Atualizar grupo de cuidado", description = "Atualiza dados do grupo. O serviço deve validar que o usuário tem autoridade sobre o grupo informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDTO> updateGroup(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para UpdateGroupDTO", required = true) @RequestBody @Valid UpdateGroupDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.updateGroup(id, dto, userLogado);
        return ResponseEntity.ok(response);
    }

    /**
     * Entrar em um grupo existente informando o código de convite.
     */
    @Operation(summary = "Ingressar em grupo", description = "Ingressa no grupo usando o código de convite, sujeito às regras de pertencimento e duplicidade.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PostMapping("/join")
    public ResponseEntity<GroupResponseDTO> joinGroup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para JoinGroupDTO", required = true) @RequestBody @Valid JoinGroupDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.joinGroup(dto, userLogado);
        return ResponseEntity.ok(response);
    }

    /**
     * Consultar informações do grupo do usuário logado.
     */
    @Operation(summary = "Consultar meu grupo", description = "Retorna o grupo de cuidado associado ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping("/me")
    public ResponseEntity<GroupResponseDTO> getMyGroup(
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.getMyGroup(userLogado);
        
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Consultar grupo por ID.
     */
    @Operation(summary = "Consultar grupo por ID", description = "Consulta um grupo somente se o usuário tiver acesso permitido a esse tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDTO> getGroupById(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.getGroupById(id, userLogado);
        return ResponseEntity.ok(response);
    }
}
