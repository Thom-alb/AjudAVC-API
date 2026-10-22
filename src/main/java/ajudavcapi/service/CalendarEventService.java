package ajudavcapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.calendarEvent.CreateCalendarEventDTO;
import ajudavcapi.domain.dto.calendarEvent.CalendarEventResponseDTO;
import ajudavcapi.domain.entity.CalendarEventEntity;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.CalendarEventRepository;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.GroupMemberRepository;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.enums.GroupRole;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public CalendarEventResponseDTO createEvent(CreateCalendarEventDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        requirePermission(group, userLogado, true);
        CalendarEventEntity event = new CalendarEventEntity();
        event.setTitle(dto.title().trim());
        event.setDescription(dto.description());
        event.setEventDateTime(dto.eventDateTime());
        event.setEventCategory(dto.eventCategory().trim());
        event.setCompleted(false);
        event.setCreatedBy(userLogado);
        event.setAssignedUser(Boolean.FALSE.equals(dto.assignToCreator()) ? null : userLogado);
        event.setGroup(group);

        CalendarEventEntity savedEvent = calendarEventRepository.save(event);
        activityLogService.createSystemLog(
                userLogado, group, "Atividade criada: " + savedEvent.getTitle(),
                "A atividade " + savedEvent.getTitle() + " foi criada na agenda.",
                "CREATE", "AUDIT");
        return mapToDTO(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponseDTO> getGroupEvents(UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        return calendarEventRepository.findByGroupIdOrderByEventDateTimeAsc(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public CalendarEventResponseDTO assignEvent(Long eventId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        CalendarEventEntity event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        if (event.getGroup() == null || !event.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: esta atividade pertence a outro grupo.");
        }
        if (event.getAssignedUser() != null) {
            throw new IllegalArgumentException("Esta atividade já possui um responsável.");
        }

        event.setAssignedUser(userLogado);
        CalendarEventEntity updated = calendarEventRepository.save(event);
        activityLogService.createSystemLog(
                userLogado, group, "Tarefa assumida: " + event.getTitle(),
                "O cuidador " + userLogado.getName() + " assumiu a atividade " + event.getTitle() + ".",
                "ASSIGN", "AUDIT");
        return mapToDTO(updated);
    }

    @Transactional
    public CalendarEventResponseDTO toggleEventCompletion(Long eventId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        CalendarEventEntity event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        if (event.getGroup() == null || !event.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: este evento pertence a outro grupo.");
        }

        event.setCompleted(!event.isCompleted());
        CalendarEventEntity updatedEvent = calendarEventRepository.save(event);
        activityLogService.createSystemLog(
                userLogado, group, "Status alterado: " + event.getTitle(),
                "A atividade " + event.getTitle() + " foi marcada como " +
                        (event.isCompleted() ? "concluída" : "pendente") + ".",
                event.isCompleted() ? "COMPLETED" : "PENDING", "AUDIT");
        return mapToDTO(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long eventId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        requirePermission(group, userLogado, true);
        CalendarEventEntity event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        if (event.getGroup() == null || !event.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: este evento pertence a outro grupo.");
        }

        activityLogService.createSystemLog(
                userLogado, group, "Atividade excluída: " + event.getTitle(),
                "A atividade " + event.getTitle() + " foi excluída da agenda.",
                "DELETE", "AUDIT");
        calendarEventRepository.delete(event);
    }

    private void requirePermission(GroupEntity group, UserEntity user, boolean routine) {
        if (group.getLeader() != null && user.getId() != null
                && group.getLeader().getId().equals(user.getId())) {
            return;
        }
        GroupMemberEntity member = groupMemberRepository.findByGroupIdAndUserId(group.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não pertence ao grupo."));
        if (member.getRole() == GroupRole.LEADER) return;
        boolean allowed = routine ? member.isCanEditRoutine() : member.isCanEditProgress();
        if (!allowed) {
            throw new IllegalArgumentException(routine
                    ? "O líder não concedeu permissão para editar a rotina."
                    : "O líder não concedeu permissão para editar o progresso.");
        }
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário autenticado não encontrado.");
        }
        if (user.getGroup() != null) {
            return user.getGroup();
        }
        return groupRepository.findByLeaderIdOrMembersUserId(user.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui nenhum grupo associado."));
    }

    private CalendarEventResponseDTO mapToDTO(CalendarEventEntity entity) {
        return new CalendarEventResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getEventDateTime(),
                entity.getEventCategory(),
                entity.isCompleted(),
                entity.getCreatedBy() != null ? entity.getCreatedBy().getName() : "Cuidador",
                entity.getGroup() != null ? entity.getGroup().getId() : null,
                entity.getAssignedUser() != null ? entity.getAssignedUser().getId() : null,
                entity.getAssignedUser() != null ? entity.getAssignedUser().getName() : null);
    }
}
