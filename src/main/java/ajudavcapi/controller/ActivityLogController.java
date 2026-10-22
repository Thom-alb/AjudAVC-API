
package ajudavcapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.activitylog.ActivityLogResponseDTO;
import ajudavcapi.domain.dto.activitylog.CreateActivityLogDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; 

@RestController
@Tag(name = "Atividades e Diário", description = "Registros de atividades, histórico e auditoria acessíveis no grupo.")
@RequestMapping("/activity-logs")
@RequiredArgsConstructor 
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @Operation(summary = "Criar registro no diário", description = "Cria um registro de atividade no contexto do grupo do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso; confira o cabeçalho Location"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Grupo ou recurso relacionado não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito, como e-mail ou associação duplicada")
        })
    @PostMapping
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<ActivityLogResponseDTO> createLog(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para CreateActivityLogDTO", required = true) @RequestBody @Valid CreateActivityLogDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        ActivityLogResponseDTO response = activityLogService.createLog(dto, userLogado);

        URI uri = uriBuilder.path("/activity-logs/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Consultar meus registros de auditoria", description = "Retorna os registros associados ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<List<ActivityLogResponseDTO>> getMyLogs(
            @AuthenticationPrincipal UserEntity userLogado) {
        return ResponseEntity.ok(activityLogService.getMyLogs(userLogado));
    }

    @GetMapping
    public ResponseEntity<List<ActivityLogResponseDTO>> getGroupLogs(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<ActivityLogResponseDTO> logs = activityLogService.getGroupLogs(userLogado);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Atualizar status do registro", description = "Atualiza o status de um registro permitido pelo serviço; registros de auditoria podem ser imutáveis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ActivityLogResponseDTO> updateStatus(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @Parameter(description = "Novo status permitido pelo domínio", example = "COMPLETED") @RequestParam String status,
            @AuthenticationPrincipal UserEntity userLogado) {

        ActivityLogResponseDTO response = activityLogService.updateStatus(id, status, userLogado);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Excluir registro do diário", description = "Exclui registro conforme regras de autoria, grupo e imutabilidade de auditoria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso excluído sem corpo de resposta"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Exclusão conflita com regra de negócio")
        })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<Void> deleteLog(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        activityLogService.deleteLog(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}
