package ajudavcapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.calendarEvent.CalendarEventResponseDTO;
import ajudavcapi.domain.dto.calendarEvent.CreateCalendarEventDTO;
import ajudavcapi.domain.entity.CalendarEventEntity;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.CalendarEventRepository;
import ajudavcapi.domain.repository.GroupRepository;

@Service
public class CalendarEventService {

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public CalendarEventResponseDTO createEvent(CreateCalendarEventDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        CalendarEventEntity event = new CalendarEventEntity();
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setEventDateTime(dto.eventDateTime());
        event.setEventCategory(dto.eventCategory());
        event.setCompleted(false);
        event.setCreatedBy(userLogado);
        event.setGroup(group);

        CalendarEventEntity savedEvent = calendarEventRepository.save(event);

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
    public CalendarEventResponseDTO toggleEventCompletion(Long eventId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        CalendarEventEntity event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        // Garante que o evento pertence ao mesmo grupo do usuário logado
        if (!event.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Este evento pertence a outro grupo.");
        }

        // Alterna entre concluído e pendente
        event.setCompleted(!event.isCompleted());
        CalendarEventEntity updatedEvent = calendarEventRepository.save(event);

        return mapToDTO(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long eventId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        CalendarEventEntity event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        if (!event.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Este evento pertence a outro grupo.");
        }

        calendarEventRepository.delete(event);
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user.getGroup() != null) {
            return user.getGroup();
        }
        return groupRepository.findByLeader(user)
                .stream()
                .findFirst()
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
            entity.getCreatedBy().getName(),
            entity.getGroup().getId()
        );
    }
}
