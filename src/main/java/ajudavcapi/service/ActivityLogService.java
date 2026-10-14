package ajudavcapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.entity.ActivityLogEntity;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.ActivityLogRepository;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.dto.activitylog.ActivityLogResponseDTO;
import ajudavcapi.domain.dto.activitylog.CreateActivityLogDTO;


@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public ActivityLogResponseDTO createLog(CreateActivityLogDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        ActivityLogEntity log = new ActivityLogEntity();
        log.setTitle(dto.title());
        log.setDescription(dto.description());
        log.setActivityDate(dto.activityDate());
        log.setCategory(dto.category());
        log.setUser(userLogado);
        log.setGroup(group);

        // Se passar status customizado usa ele, senão mantém o default "PENDING"
        if (dto.status() != null && !dto.status().isBlank()) {
            log.setStatus(dto.status().toUpperCase());
        }

        ActivityLogEntity savedLog = activityLogRepository.save(log);

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
        ActivityLogEntity log = new ActivityLogEntity();
        log.setTitle(title);
        log.setDescription(description);
        log.setActivityDate(java.time.LocalDateTime.now());
        log.setStatus(action == null || action.isBlank() ? "RECORDED" : action.toUpperCase());
        log.setCategory(category == null || category.isBlank() ? "AUDIT" : category.toUpperCase());
        log.setUser(user);
        log.setGroup(group);
        log.setCreatedAt(java.time.LocalDateTime.now());
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponseDTO> getMyLogs(UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        return activityLogRepository.findByGroupIdAndUserIdOrderByCreatedAtDesc(
                        group.getId(), userLogado.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponseDTO> getGroupLogs(UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        return activityLogRepository.findByGroupIdOrderByActivityDateDesc(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public ActivityLogResponseDTO updateStatus(Long logId, String newStatus, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        ActivityLogEntity log = activityLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada."));

        if (!log.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Esta atividade pertence a outro grupo.");
        }
        if ("AUDIT".equalsIgnoreCase(log.getCategory())) {
            throw new IllegalArgumentException("Registros de auditoria não podem ser alterados.");
        }

        log.setStatus(newStatus.toUpperCase());
        ActivityLogEntity updatedLog = activityLogRepository.save(log);

        return mapToDTO(updatedLog);
    }

    @Transactional
    public void deleteLog(Long logId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        ActivityLogEntity log = activityLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada."));

        if (!log.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Esta atividade pertence a outro grupo.");
        }
        if ("AUDIT".equalsIgnoreCase(log.getCategory())) {
            throw new IllegalArgumentException("Registros de auditoria não podem ser excluídos.");
        }

        activityLogRepository.delete(log);
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user.getGroup() != null) {
            return user.getGroup();
        }
        return groupRepository.findByLeaderIdOrMembersUserId(user.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui nenhum grupo associado."));
    }

    private ActivityLogResponseDTO mapToDTO(ActivityLogEntity entity) {
        return new ActivityLogResponseDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getActivityDate(),
            entity.getStatus(),
            entity.getCategory(),
            entity.getCreatedAt(),
            entity.getUser().getName(),
            entity.getGroup().getId()
        );
    }
}