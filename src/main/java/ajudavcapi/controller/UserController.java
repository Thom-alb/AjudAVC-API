
package ajudavcapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.user.UserRequestDTO;
import ajudavcapi.domain.dto.user.UserResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "Gestão de Usuários", description = "Operações administrativas de cadastro e manutenção de usuários.")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(summary = "Listar usuários", description = "Lista usuários. Atenção: a implementação atual não mostra restrição explícita por usuário/grupo; proteja este endpoint conforme o modelo de privacidade esperado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getTodos() {
        List<UserResponseDTO> lista = service.listarUsuarios().stream()
            .map(UserResponseDTO::new)
            .toList();

        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Consultar usuário por ID", description = "Retorna dados do usuário informado. Restringir acesso conforme política de privacidade.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> buscarPorId(@Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id) {
        UserEntity user = service.buscarPorId(id);
        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    @Operation(summary = "Cadastrar usuário", description = "Cria um usuário e retorna a representação criada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso; confira o cabeçalho Location"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Grupo ou recurso relacionado não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito, como e-mail ou associação duplicada")
        })
    @PostMapping
    public ResponseEntity<UserResponseDTO> cadastrar(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para UserRequestDTO", required = true) @RequestBody @Valid UserRequestDTO dto, UriComponentsBuilder uriBuilder) {
        UserEntity novoUsuario = service.adicionarUsuario(dto);
        
        URI uri = uriBuilder.path("/user/{id}").buildAndExpand(novoUsuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserResponseDTO(novoUsuario));
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza o usuário informado. Recomenda-se validar se o usuário autenticado pode alterar esse ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> atualizar(@Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id, @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para UserRequestDTO", required = true) @RequestBody @Valid UserRequestDTO dto) {
        UserEntity usuarioAtualizado = service.atualizarUsuario(id, dto);
        return ResponseEntity.ok(new UserResponseDTO(usuarioAtualizado));
    }

    @Operation(summary = "Excluir usuário", description = "Exclui o usuário informado. Recomenda-se restringir a operação ao próprio usuário ou a administrador autorizado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso excluído sem corpo de resposta"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Exclusão conflita com regra de negócio")
        })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id) {
        service.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
