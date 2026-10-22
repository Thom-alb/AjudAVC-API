
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.weeklyProgress.CreateWeeklyProgressDTO;
import ajudavcapi.domain.dto.weeklyProgress.WeeklyProgressResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.WeeklyProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "Progresso Semanal", description = "Registros de evolução semanal do paciente e indicadores de atividades.")
@RequestMapping("/weekly-progress")
@RequiredArgsConstructor
public class WeeklyProgressController {

    private final WeeklyProgressService weeklyProgressService;

    // Registrar uma nova avaliação de progresso semanal (Líder ou Membro)
    @Operation(summary = "Registrar progresso semanal", description = "Registra uma avaliação de comunicação, mobilidade, memória, humor e descrição. O usuário precisa pertencer ao grupo; regras de edição adicionais são validadas pelo serviço.")
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
    public ResponseEntity<WeeklyProgressResponseDTO> createProgress(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para CreateWeeklyProgressDTO", required = true) @RequestBody @Valid CreateWeeklyProgressDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        WeeklyProgressResponseDTO response = weeklyProgressService.createProgress(dto, userLogado);

        URI uri = uriBuilder.path("/weekly-progress/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Listar o histórico de avaliações do grupo para renderizar os gráficos/evolução no app
    @Operation(summary = "Consultar histórico de progresso", description = "Retorna os registros de progresso do grupo do usuário autenticado, respeitando o isolamento por grupo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping
    public ResponseEntity<List<WeeklyProgressResponseDTO>> getGroupProgressHistory(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<WeeklyProgressResponseDTO> history = weeklyProgressService.getGroupProgressHistory(userLogado);
        return ResponseEntity.ok(history);
    }

    // Deletar um registro do histórico
    @Operation(summary = "Excluir registro de progresso", description = "Exclui um registro acessível no grupo conforme as regras de autorização do serviço.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso excluído sem corpo de resposta"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Exclusão conflita com regra de negócio")
        })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<Void> deleteProgress(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        weeklyProgressService.deleteProgress(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}
