package ajudavcapi.controller;

import ajudavcapi.domain.dto.monthlySummary.MonthlySummaryResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.MonthlySummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monthly-summaries")
public class MonthlySummaryController {

    @Autowired
    private MonthlySummaryService monthlySummaryService;

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