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

        activityLogRepository.delete(log);
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
