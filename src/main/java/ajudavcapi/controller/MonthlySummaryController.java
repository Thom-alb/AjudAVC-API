package ajudavcapi.controller;

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
@RequestMapping("/monthly-summaries")
@RequiredArgsConstructor
public class MonthlySummaryController {

    private final MonthlySummaryService monthlySummaryService;

    // Retorna o resumo consolidado do mês (calculado em tempo real com base nos registros do mês)
    @GetMapping("/filter")
    public ResponseEntity<MonthlySummaryResponseDTO> getSummaryByMonthAndYear(
            @RequestParam Integer month,
            @RequestParam Integer year,
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