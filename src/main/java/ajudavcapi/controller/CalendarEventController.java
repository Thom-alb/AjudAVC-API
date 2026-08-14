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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.calendarEvent.CalendarEventResponseDTO;
import ajudavcapi.domain.dto.calendarEvent.CreateCalendarEventDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.CalendarEventService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/calendar-events")
public class CalendarEventController {

    @Autowired
    private CalendarEventService calendarEventService;

    // Criar um evento na agenda do grupo
    @PostMapping
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<CalendarEventResponseDTO> createEvent(
            @RequestBody @Valid CreateCalendarEventDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        CalendarEventResponseDTO response = calendarEventService.createEvent(dto, userLogado);

        URI uri = uriBuilder.path("/calendar-events/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Listar todos os eventos da agenda do grupo do usuário
    @GetMapping
    public ResponseEntity<List<CalendarEventResponseDTO>> getGroupEvents(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<CalendarEventResponseDTO> events = calendarEventService.getGroupEvents(userLogado);
        return ResponseEntity.ok(events);
    }

    // Marcar/Desmarcar evento como concluído (ex: medicamento tomado)
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<CalendarEventResponseDTO> toggleEventCompletion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        CalendarEventResponseDTO response = calendarEventService.toggleEventCompletion(id, userLogado);
        return ResponseEntity.ok(response);
    }

    // Deletar um evento (Somente o Líder)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        calendarEventService.deleteEvent(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}
