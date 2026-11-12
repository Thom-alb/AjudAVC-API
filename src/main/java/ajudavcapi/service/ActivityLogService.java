
package ajudavcapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.activitylog.ActivityLogResponseDTO;
import ajudavcapi.domain.dto.activitylog.CreateActivityLogDTO;
import ajudavcapi.domain.entity.ActivityLogEntity;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.GroupStatus;
import ajudavcapi.domain.repository.ActivityLogRepository;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.exception.ConflictException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public ActivityLogResponseDTO createLog(
            CreateActivityLogDTO dto,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);

        ActivityLogEntity log = new ActivityLogEntity();

        log.setTitle(dto.title());
        log.setDescription(dto.description());
        log.setActivityDate(dto.activityDate());
        log.setCategory(dto.category());
        log.setUser(userLogado);
        log.setGroup(group);

        if (dto.status() != null && !dto.status().isBlank()) {
            log.setStatus(dto.status().toUpperCase());
        }

        ActivityLogEntity savedLog =
                activityLogRepository.save(log);

        return mapToDTO(savedLog);
    }

    @Transactional
    public void createSystemLog(
            UserEntity user,
            GroupEntity group,
            String title,
            String description,
            String action,
            String category) {

        ensureGroupActive(group);

        ActivityLogEntity log = new ActivityLogEntity();

        log.setTitle(title);
        log.setDescription(description);
        log.setActivityDate(LocalDateTime.now());

        log.setStatus(
                action == null || action.isBlank()
                        ? "RECORDED"
                        : action.toUpperCase());

        log.setCategory(
                category == null || category.isBlank()
                        ? "AUDIT"
                        : category.toUpperCase());

        log.setUser(user);
        log.setGroup(group);
        log.setCreatedAt(LocalDateTime.now());

        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponseDTO> getMyLogs(
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);

        return activityLogRepository
                .findByGroupIdAndUserIdOrderByCreatedAtDesc(
                        group.getId(),
                        userLogado.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponseDTO> getGroupLogs(
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);

        return activityLogRepository
                .findByGroupIdOrderByActivityDateDesc(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public ActivityLogResponseDTO updateStatus(
            Long logId,
            String newStatus,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);

        ActivityLogEntity log = findLogInGroup(logId, group);

        requireOwnershipOrLeader(log, group, userLogado);
        requireNotAudit(log);

        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException(
                    "O novo status é obrigatório.");
        }

        log.setStatus(newStatus.trim().toUpperCase());

        ActivityLogEntity updatedLog =
                activityLogRepository.save(log);

        return mapToDTO(updatedLog);
    }

    @Transactional
    public void deleteLog(
            Long logId,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);

        ActivityLogEntity log = findLogInGroup(logId, group);

        requireOwnershipOrLeader(log, group, userLogado);
        requireNotAudit(log);

        activityLogRepository.delete(log);
    }

    private void ensureGroupActive(GroupEntity group) {

        if (group == null
                || group.getStatus() == GroupStatus.ARCHIVED) {

            throw new ConflictException(
                    "Este grupo está arquivado e permite somente consultas.");
        }
    }

    private ActivityLogEntity findLogInGroup(
            Long logId,
            GroupEntity group) {

        ActivityLogEntity log = activityLogRepository
                .findById(logId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atividade não encontrada."));

        if (log.getGroup() == null
                || !log.getGroup().getId().equals(group.getId())) {

            throw new AccessDeniedException(
                    "Você não possui acesso a este registro.");
        }

        return log;
    }

    private void requireOwnershipOrLeader(
            ActivityLogEntity log,
            GroupEntity group,
            UserEntity user) {

        boolean isLeader = group.getLeader() != null
                && group.getLeader().getId().equals(user.getId());

        boolean isOwner = log.getUser() != null
                && log.getUser().getId().equals(user.getId());

        if (!isLeader && !isOwner) {
            throw new AccessDeniedException(
                    "Você só pode alterar ou excluir seus próprios registros.");
        }
    }

    private void requireNotAudit(ActivityLogEntity log) {

        if ("AUDIT".equalsIgnoreCase(log.getCategory())) {
            throw new AccessDeniedException(
                    "Registros de auditoria não podem ser alterados ou excluídos.");
        }
    }

    private GroupEntity getUserGroup(UserEntity user) {

        if (user == null || user.getId() == null) {
            throw new AccessDeniedException(
                    "Usuário autenticado não encontrado.");
        }

        return groupRepository
                .findByLeaderIdOrMembersUserId(
                        user.getId(),
                        user.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não possui nenhum grupo associado."));
    }

    private ActivityLogResponseDTO mapToDTO(
            ActivityLogEntity entity) {

        return new ActivityLogResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getActivityDate(),
                entity.getStatus(),
                entity.getCategory(),
                entity.getCreatedAt(),
                entity.getUser() != null
                        ? entity.getUser().getName()
                        : "Sistema",
                entity.getGroup().getId());
    }
}
