package ajudavcapi.service;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.weeklyProgress.CreateWeeklyProgressDTO;
import ajudavcapi.domain.dto.weeklyProgress.WeeklyProgressResponseDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;
import ajudavcapi.domain.repository.CalendarEventRepository;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.WeeklyProgressRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeeklyProgressService {

    private final WeeklyProgressRepository weeklyProgressRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public WeeklyProgressResponseDTO createProgress(CreateWeeklyProgressDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        LocalDateTime now = LocalDateTime.now();

        WeeklyProgressEntity entity = new WeeklyProgressEntity();
        entity.setUser(userLogado);
        entity.setGroup(group);
        entity.setCommunicationScore(dto.communicationScore());
        entity.setMobilityScore(dto.mobilityScore());
        entity.setMemoryScore(dto.memoryScore());
        entity.setCountAnimo(nonNegative(dto.countAnimo()));
        entity.setCountFeliz(nonNegative(dto.countFeliz()));
        entity.setCountApatia(nonNegative(dto.countApatia()));
        entity.setCountRaiva(nonNegative(dto.countRaiva()));
        entity.setCountTriste(nonNegative(dto.countTriste()));
        entity.setDescription(dto.description());
        entity.setCreatedAt(now);
        entity.setWeekOfMonth(now.get(WeekFields.of(Locale.getDefault()).weekOfMonth()));

        return mapToDTO(weeklyProgressRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<WeeklyProgressResponseDTO> getGroupProgressHistory(UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        return weeklyProgressRepository.findByGroupIdOrderByCreatedAtDesc(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void deleteProgress(Long id, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        WeeklyProgressEntity entity = weeklyProgressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de progresso não encontrado."));

        if (!entity.getGroup().getId().equals(group.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Acesso negado: registro pertence a outro grupo.");
        }
        weeklyProgressRepository.delete(entity);
    }

    private WeeklyProgressResponseDTO mapToDTO(WeeklyProgressEntity entity) {
        LocalDateTime end = entity.getCreatedAt();
        LocalDateTime start = end.minusDays(7);
        Long groupId = entity.getGroup().getId();

        int planned = Math.toIntExact(calendarEventRepository
                .countByGroupIdAndEventDateTimeGreaterThanEqualAndEventDateTimeLessThanEqual(
                        groupId, start, end));
        int completed = Math.toIntExact(calendarEventRepository
                .countByGroupIdAndEventDateTimeGreaterThanEqualAndEventDateTimeLessThanEqualAndCompletedTrue(
                        groupId, start, end));

        return new WeeklyProgressResponseDTO(
                entity.getId(),
                entity.getCommunicationScore(),
                entity.getMobilityScore(),
                entity.getMemoryScore(),
                valueOrZero(entity.getCountAnimo()),
                valueOrZero(entity.getCountFeliz()),
                valueOrZero(entity.getCountApatia()),
                valueOrZero(entity.getCountRaiva()),
                valueOrZero(entity.getCountTriste()),
                entity.getDescription(),
                entity.getWeekOfMonth(),
                entity.getCreatedAt(),
                entity.getUser().getName(),
                groupId,
                planned,
                completed
        );
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user.getGroup() != null) {
            return user.getGroup();
        }
        return groupRepository.findByLeader(user).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui nenhum grupo associado."));
    }

    private Integer nonNegative(Integer value) {
        if (value == null) return 0;
        if (value < 0) throw new IllegalArgumentException("A contagem de humor não pode ser negativa.");
        return value;
    }

    private Integer valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
