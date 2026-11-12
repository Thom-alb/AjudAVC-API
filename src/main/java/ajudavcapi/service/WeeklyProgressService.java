
package ajudavcapi.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.weeklyProgress.CreateWeeklyProgressDTO;
import ajudavcapi.domain.dto.weeklyProgress.WeeklyProgressResponseDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.domain.enums.GroupStatus;
import ajudavcapi.domain.mapper.WeeklyProgressMapper;
import ajudavcapi.domain.repository.CalendarEventRepository;
import ajudavcapi.domain.repository.GroupMemberRepository;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.WeeklyProgressRepository;
import ajudavcapi.exception.ConflictException;
import ajudavcapi.exception.ResourceNotFoundException;

@Service
public class WeeklyProgressService {

    private final WeeklyProgressRepository weeklyProgressRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final GroupRepository groupRepository;
    private final WeeklyProgressMapper weeklyProgressMapper;
    private final GroupMemberRepository groupMemberRepository;
    private final ActivityLogService activityLogService;

    public WeeklyProgressService(
            WeeklyProgressRepository weeklyProgressRepository,
            CalendarEventRepository calendarEventRepository,
            GroupRepository groupRepository,
            WeeklyProgressMapper weeklyProgressMapper,
            GroupMemberRepository groupMemberRepository,
            ActivityLogService activityLogService) {

        this.weeklyProgressRepository = weeklyProgressRepository;
        this.calendarEventRepository = calendarEventRepository;
        this.groupRepository = groupRepository;
        this.weeklyProgressMapper = weeklyProgressMapper;
        this.groupMemberRepository = groupMemberRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public WeeklyProgressResponseDTO createProgress(
            CreateWeeklyProgressDTO dto,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);

        requireProgressPermission(group, userLogado);

        WeeklyProgressEntity entity = weeklyProgressRepository
                .findByGroupIdAndYearAndMonthAndWeekOfMonth(
                        group.getId(),
                        dto.year(),
                        dto.month(),
                        dto.weekOfMonth())
                .orElseGet(WeeklyProgressEntity::new);

        boolean isNew = entity.getId() == null;

        entity.setUser(userLogado);
        entity.setGroup(group);
        entity.setYear(dto.year());
        entity.setMonth(dto.month());
        entity.setWeekOfMonth(dto.weekOfMonth());

        entity.setCommunicationScore(dto.communicationScore());
        entity.setMobilityScore(dto.mobilityScore());
        entity.setMemoryScore(dto.memoryScore());

        entity.setCountAnimo(
                dto.countAnimo() != null ? dto.countAnimo() : 0);
        entity.setCountFeliz(
                dto.countFeliz() != null ? dto.countFeliz() : 0);
        entity.setCountApatia(
                dto.countApatia() != null ? dto.countApatia() : 0);
        entity.setCountRaiva(
                dto.countRaiva() != null ? dto.countRaiva() : 0);
        entity.setCountTriste(
                dto.countTriste() != null ? dto.countTriste() : 0);

        entity.setDescription(dto.description());

        if (isNew) {
            entity.setCreatedAt(LocalDateTime.now());
        }

        WeeklyProgressEntity saved =
                weeklyProgressRepository.save(entity);

        activityLogService.createSystemLog(
                userLogado,
                group,
                isNew
                        ? "Progresso semanal registrado"
                        : "Progresso semanal atualizado",
                "Foi " + (isNew ? "registrado" : "atualizado")
                        + " o acompanhamento da Semana "
                        + dto.weekOfMonth()
                        + " do mês "
                        + dto.month()
                        + "/"
                        + dto.year()
                        + ".",
                isNew ? "CREATE_PROGRESS" : "UPDATE_PROGRESS",
                "AUDIT");

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<WeeklyProgressResponseDTO> getGroupProgressHistory(
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);

        return weeklyProgressRepository
                .findByGroupIdFetchUser(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void deleteProgress(
            Long id,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);

        requireProgressPermission(group, userLogado);

        WeeklyProgressEntity entity = weeklyProgressRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registro de progresso não encontrado."));

        if (entity.getGroup() == null
                || !entity.getGroup().getId().equals(group.getId())) {

            throw new AccessDeniedException(
                    "Você não tem permissão para acessar este registro.");
        }

        activityLogService.createSystemLog(
                userLogado,
                group,
                "Registro de progresso excluído",
                "Um registro de progresso semanal foi excluído.",
                "DELETE_PROGRESS",
                "AUDIT");

        weeklyProgressRepository.delete(entity);
    }

    private void ensureGroupActive(GroupEntity group) {

        if (group == null
                || group.getStatus() == GroupStatus.ARCHIVED) {

            throw new ConflictException(
                    "Este grupo está arquivado e permite somente consultas.");
        }
    }

    private void requireProgressPermission(
            GroupEntity group,
            UserEntity user) {

        if (group.getLeader() != null
                && group.getLeader().getId() != null
                && group.getLeader().getId().equals(user.getId())) {
            return;
        }

        GroupMemberEntity member = groupMemberRepository
                .findByGroupIdAndUserId(group.getId(), user.getId())
                .orElseThrow(() -> new AccessDeniedException(
                        "Usuário não pertence a este grupo."));

        if (member.getRole() == GroupRole.LEADER
                || member.isCanEditProgress()) {
            return;
        }

        throw new AccessDeniedException(
                "O líder não concedeu permissão para editar o progresso semanal.");
    }

    private WeeklyProgressResponseDTO mapToDTO(
            WeeklyProgressEntity entity) {

        Long groupId = entity.getGroup().getId();

        LocalDateTime referenceDate = entity.getCreatedAt() != null
                ? entity.getCreatedAt()
                : LocalDateTime.now();

        LocalDateTime startPeriod = referenceDate.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();

        LocalDateTime endPeriod = startPeriod
                .plusDays(7)
                .minusNanos(1);

        int planned = Math.toIntExact(
                calendarEventRepository
                        .countByGroupIdAndEventDateTimeBetween(
                                groupId,
                                startPeriod,
                                endPeriod));

        int completed = Math.toIntExact(
                calendarEventRepository
                        .countCompletedByGroupIdAndEventDateTimeBetween(
                                groupId,
                                startPeriod,
                                endPeriod));

        return weeklyProgressMapper.toResponseDTO(
                entity,
                planned,
                completed);
    }

    private GroupEntity getUserGroup(UserEntity user) {

        if (user == null || user.getId() == null) {
            throw new AccessDeniedException(
                    "Usuário não autenticado.");
        }

        return groupRepository
                .findByLeaderIdOrMembersUserId(
                        user.getId(),
                        user.getId())
                .orElseThrow(() -> new AccessDeniedException(
                        "Usuário não possui nenhum grupo associado."));
    }
}
