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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.WeeklyProgressService;
import ajudavcapi.domain.dto.weeklyProgress.CreateWeeklyProgressDTO;
import ajudavcapi.domain.dto.weeklyProgress.WeeklyProgressResponseDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/weekly-progress")
public class WeeklyProgressController {

    @Autowired
    private WeeklyProgressService weeklyProgressService;

    // Registrar uma nova avaliação de progresso semanal (Líder ou Membro)
    @PostMapping
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<WeeklyProgressResponseDTO> createProgress(
            @RequestBody @Valid CreateWeeklyProgressDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        WeeklyProgressResponseDTO response = weeklyProgressService.createProgress(dto, userLogado);

        URI uri = uriBuilder.path("/weekly-progress/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Listar o histórico de avaliações do grupo para renderizar os gráficos/evolução no app
    @GetMapping
    public ResponseEntity<List<WeeklyProgressResponseDTO>> getGroupProgressHistory(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<WeeklyProgressResponseDTO> history = weeklyProgressService.getGroupProgressHistory(userLogado);
        return ResponseEntity.ok(history);
    }

    // Deletar um registro do histórico
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<Void> deleteProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        weeklyProgressService.deleteProgress(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}
