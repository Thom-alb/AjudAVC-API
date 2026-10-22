
package ajudavcapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Agenda e Calendário", description = "Atividades e eventos de cuidado, atribuição e conclusão.")
@RequestMapping("/calendar-events")
public class CalendarEventController {

    @Autowired
    private CalendarEventService calendarEventService;

    // Criar um evento na agenda do grupo
    @Operation(summary = "Criar evento de agenda", description = "Cria um evento para o grupo. O evento pode ficar sem responsável ou ser atribuído ao criador.")
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
    public ResponseEntity<CalendarEventResponseDTO> createEvent(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para CreateCalendarEventDTO", required = true) @RequestBody @Valid CreateCalendarEventDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        CalendarEventResponseDTO response = calendarEventService.createEvent(dto, userLogado);

        URI uri = uriBuilder.path("/calendar-events/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Listar todos os eventos da agenda do grupo do usuário
    @Operation(summary = "Listar agenda do grupo", description = "Lista eventos da agenda do grupo do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping
    public ResponseEntity<List<CalendarEventResponseDTO>> getGroupEvents(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<CalendarEventResponseDTO> events = calendarEventService.getGroupEvents(userLogado);
        return ResponseEntity.ok(events);
    }

    // Marcar/Desmarcar evento como concluído (ex: medicamento tomado)
    @Operation(summary = "Alternar conclusão do evento", description = "Marca ou desmarca o evento como concluído, se o evento pertencer ao grupo acessível.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<CalendarEventResponseDTO> toggleEventCompletion(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        CalendarEventResponseDTO response = calendarEventService.toggleEventCompletion(id, userLogado);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Assumir evento sem responsável", description = "Atribui o evento ao usuário autenticado quando ainda não há responsável e as regras do serviço permitem.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('LEADER', 'MEMBER')")
    public ResponseEntity<CalendarEventResponseDTO> assignEvent(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {
        return ResponseEntity.ok(calendarEventService.assignEvent(id, userLogado));
    }

    // Deletar um evento (Somente o Líder)
    @Operation(summary = "Excluir evento", description = "Exclui evento conforme permissões de edição da rotina e regras do grupo; a autorização é validada no serviço.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso excluído sem corpo de resposta"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Exclusão conflita com regra de negócio")
        })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        calendarEventService.deleteEvent(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}
