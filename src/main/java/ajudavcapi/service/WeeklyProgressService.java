package ajudavcapi.service;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.weeklyProgress.CreateWeeklyProgressDTO;
import ajudavcapi.domain.dto.weeklyProgress.WeeklyProgressResponseDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;
import ajudavcapi.domain.mapper.WeeklyProgressMapper;
import ajudavcapi.domain.repository.CalendarEventRepository;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.WeeklyProgressRepository;
import ajudavcapi.domain.repository.GroupMemberRepository;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.enums.GroupRole;

@Service
public class WeeklyProgressService {

    private final WeeklyProgressRepository weeklyProgressRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final GroupRepository groupRepository;
    private final WeeklyProgressMapper weeklyProgressMapper;
    private final GroupMemberRepository groupMemberRepository;

    @Autowired
    private ActivityLogService activityLogService;

    // Injeção via Construtor garantindo imunidade a NullPointer e facilidade para Mocks
    public WeeklyProgressService(
            WeeklyProgressRepository weeklyProgressRepository,
            CalendarEventRepository calendarEventRepository,
            GroupRepository groupRepository,
            WeeklyProgressMapper weeklyProgressMapper,
            GroupMemberRepository groupMemberRepository) {
        this.weeklyProgressRepository = weeklyProgressRepository;
        this.calendarEventRepository = calendarEventRepository;
        this.groupRepository = groupRepository;
        this.weeklyProgressMapper = weeklyProgressMapper;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional
    public WeeklyProgressResponseDTO createProgress(CreateWeeklyProgressDTO dto, UserEntity userLogado) {
        WeeklyProgressEntity entity = new WeeklyProgressEntity();
        entity.setUser(userLogado);

        GroupEntity group = getUserGroup(userLogado);
        requireProgressPermission(group, userLogado);
        entity.setGroup(group);

        entity.setCommunicationScore(dto.communicationScore());
        entity.setMobilityScore(dto.mobilityScore());
        entity.setMemoryScore(dto.memoryScore());

        entity.setCountAnimo(dto.countAnimo() != null ? dto.countAnimo() : 0);
        entity.setCountFeliz(dto.countFeliz() != null ? dto.countFeliz() : 0);
        entity.setCountApatia(dto.countApatia() != null ? dto.countApatia() : 0);
        entity.setCountRaiva(dto.countRaiva() != null ? dto.countRaiva() : 0);
        entity.setCountTriste(dto.countTriste() != null ? dto.countTriste() : 0);
        entity.setDescription(dto.description());

        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);

        int weekOfMonth = now.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
        entity.setWeekOfMonth(weekOfMonth);

        WeeklyProgressEntity saved = weeklyProgressRepository.save(entity);
        activityLogService.createSystemLog(
                userLogado, group, "Progresso semanal registrado",
                "Foi registrado um novo acompanhamento semanal do paciente.",
                "CREATE_PROGRESS", "AUDIT");
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<WeeklyProgressResponseDTO> getGroupProgressHistory(UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        
        // Barreira Multi-Tenant Estrita: A query força a busca apenas pelo ID do grupo do usuário logado
        return weeklyProgressRepository.findByGroupIdFetchUser(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void deleteProgress(Long id, UserEntity userLogado) {
        WeeklyProgressEntity entity = weeklyProgressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de progresso não encontrado"));

        GroupEntity group = getUserGroup(userLogado);
        requireProgressPermission(group, userLogado);

        if (!entity.getGroup().getId().equals(group.getId())) {
            throw new RuntimeException("Acesso negado: registro pertence a outro grupo");
        }

        activityLogService.createSystemLog(
                userLogado, group, "Registro de progresso excluído",
                "Um registro de progresso semanal foi excluído.",
                "DELETE_PROGRESS", "AUDIT");
        weeklyProgressRepository.delete(entity);
    }

    private void requireProgressPermission(GroupEntity group, UserEntity user) {
        if (group.getLeader() != null && user.getId() != null
                && group.getLeader().getId().equals(user.getId())) {
            return;
        }
        GroupMemberEntity member = groupMemberRepository.findByGroupIdAndUserId(group.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não pertence ao grupo."));
        if (member.getRole() == GroupRole.LEADER || member.isCanEditProgress()) {
            return;
        }
        throw new IllegalArgumentException("O líder não concedeu permissão para editar o progresso.");
    }

    private WeeklyProgressResponseDTO mapToDTO(WeeklyProgressEntity entity) {
        Long groupId = entity.getGroup().getId();
        LocalDateTime createdAt = entity.getCreatedAt();
        LocalDateTime startPeriod = createdAt.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        LocalDateTime endPeriod = startPeriod.plusDays(7).minusNanos(1);

        int planned = Math.toIntExact(
                calendarEventRepository.countByGroupIdAndEventDateTimeBetween(groupId, startPeriod, endPeriod));
        int completed = Math.toIntExact(
                calendarEventRepository.countCompletedByGroupIdAndEventDateTimeBetween(groupId, startPeriod, endPeriod));

        return weeklyProgressMapper.toResponseDTO(entity, planned, completed);
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user.getGroup() != null) {
            return user.getGroup();
        }
        return groupRepository.findByLeaderIdOrMembersUserId(user.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui nenhum grupo associado."));
    }
}