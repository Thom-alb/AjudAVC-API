package ajudavcapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.MonthlySummaryService;
import ajudavcapi.domain.dto.monthlySummary.CreateMonthlySummaryDTO;
import ajudavcapi.domain.dto.monthlySummary.MonthlySummaryResponseDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/monthly-summaries")
public class MonthlySummaryController {

    @Autowired
    private MonthlySummaryService monthlySummaryService;

    // Criar ou atualizar resumo mensal
    @PostMapping
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<MonthlySummaryResponseDTO> createOrUpdateSummary(
            @RequestBody @Valid CreateMonthlySummaryDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        MonthlySummaryResponseDTO response = monthlySummaryService.createOrUpdateSummary(dto, userLogado);

        URI uri = uriBuilder.path("/monthly-summaries/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Listar histórico de resumos mensais do grupo
    @GetMapping
    public ResponseEntity<List<MonthlySummaryResponseDTO>> getGroupSummaries(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<MonthlySummaryResponseDTO> summaries = monthlySummaryService.getGroupSummaries(userLogado);
        return ResponseEntity.ok(summaries);
    }

    // Buscar resumo específico de um mês e ano (Ex: /monthly-summaries/filter?month=7&year=2026)
    @GetMapping("/filter")
    public ResponseEntity<MonthlySummaryResponseDTO> getSummaryByMonthAndYear(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @AuthenticationPrincipal UserEntity userLogado) {

        MonthlySummaryResponseDTO response = monthlySummaryService.getSummaryByMonthAndYear(month, year, userLogado);
        return ResponseEntity.ok(response);
    }

    // Deletar resumo
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<Void> deleteSummary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        monthlySummaryService.deleteSummary(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}
