package ajudavcapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.WeeklyProgressRepository;
import ajudavcapi.domain.dto.weeklyProgress.CreateWeeklyProgressDTO;
import ajudavcapi.domain.dto.weeklyProgress.WeeklyProgressResponseDTO;


@Service
public class WeeklyProgressService {

    @Autowired
    private WeeklyProgressRepository weeklyProgressRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public WeeklyProgressResponseDTO createProgress(CreateWeeklyProgressDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        WeeklyProgressEntity progress = new WeeklyProgressEntity();
        progress.setGroup(group);
        progress.setUser(userLogado);
        progress.setCommunicationScore(dto.communicationScore());
        progress.setMobilityScore(dto.mobilityScore());
        progress.setMemoryScore(dto.memoryScore());
        progress.setMoodState(dto.moodState());
        progress.setDescription(dto.description());
        progress.setCreatedAt(LocalDateTime.now());

        WeeklyProgressEntity savedProgress = weeklyProgressRepository.save(progress);

        return mapToDTO(savedProgress);
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

        WeeklyProgressEntity progress = weeklyProgressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação de progresso não encontrada."));

        if (!progress.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Esta avaliação pertence a outro grupo.");
        }

        weeklyProgressRepository.delete(progress);
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

    private WeeklyProgressResponseDTO mapToDTO(WeeklyProgressEntity entity) {
        return new WeeklyProgressResponseDTO(
            entity.getId(),
            entity.getCommunicationScore(),
            entity.getMobilityScore(),
            entity.getMemoryScore(),
            entity.getMoodState(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUser().getName(),
            entity.getGroup().getId()
        );
    }
}