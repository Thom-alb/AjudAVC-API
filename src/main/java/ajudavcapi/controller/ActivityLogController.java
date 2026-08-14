package ajudavcapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.ActivityLogService;
import ajudavcapi.domain.dto.activitylog.ActivityLogResponseDTO;
import ajudavcapi.domain.dto.activitylog.CreateActivityLogDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<ActivityLogResponseDTO> createLog(
            @RequestBody @Valid CreateActivityLogDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        ActivityLogResponseDTO response = activityLogService.createLog(dto, userLogado);

        URI uri = uriBuilder.path("/activity-logs/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ActivityLogResponseDTO>> getGroupLogs(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<ActivityLogResponseDTO> logs = activityLogService.getGroupLogs(userLogado);
        return ResponseEntity.ok(logs);
    }

    // Altera o status (ex: /activity-logs/1/status?status=COMPLETED)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ActivityLogResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserEntity userLogado) {

        ActivityLogResponseDTO response = activityLogService.updateStatus(id, status, userLogado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<Void> deleteLog(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        activityLogService.deleteLog(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}