
package ajudavcapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajudavcapi.domain.dto.monthlySummary.MonthlySummaryResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.MonthlySummaryService;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "Resumos Mensais", description = "Indicadores agregados mensais do grupo de cuidado.")
@RequestMapping("/monthly-summaries")
@RequiredArgsConstructor
public class MonthlySummaryController {

    private final MonthlySummaryService monthlySummaryService;

    // Retorna o resumo consolidado do mês (calculado em tempo real com base nos registros do mês)
    @Operation(summary = "Consultar resumo mensal", description = "Calcula ou retorna o resumo do grupo para o mês e ano informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping("/filter")
    public ResponseEntity<MonthlySummaryResponseDTO> getSummaryByMonthAndYear(
            @Parameter(description = "Mês do resumo, de 1 a 12", example = "10") @RequestParam Integer month,
            @Parameter(description = "Ano do resumo no formato YYYY", example = "2026") @RequestParam Integer year,
            @AuthenticationPrincipal UserEntity userLogado) {

        MonthlySummaryResponseDTO response = monthlySummaryService.getOrCalculateSummary(month, year, userLogado);
        return ResponseEntity.ok(response);
    }

    // Histórico de todos os resumos do grupo
    @GetMapping
    public ResponseEntity<List<MonthlySummaryResponseDTO>> getGroupSummaries(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<MonthlySummaryResponseDTO> summaries = monthlySummaryService.getGroupSummaries(userLogado);
        return ResponseEntity.ok(summaries);
    }
}
