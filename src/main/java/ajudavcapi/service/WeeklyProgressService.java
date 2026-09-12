package ajudavcapi.service;

import ajudavcapi.domain.dto.weeklyProgress.CreateWeeklyProgressDTO;
import ajudavcapi.domain.dto.weeklyProgress.WeeklyProgressResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;
import ajudavcapi.domain.repository.WeeklyProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
public class WeeklyProgressService {

    @Autowired
    private WeeklyProgressRepository weeklyProgressRepository;

    @Transactional
    public WeeklyProgressResponseDTO createProgress(CreateWeeklyProgressDTO dto, UserEntity userLogado) {
        WeeklyProgressEntity entity = new WeeklyProgressEntity();
        
        entity.setUser(userLogado);
        entity.setGroup(userLogado.getGroup()); // Assume que o usuário pertence a um grupo
        entity.setCommunicationScore(dto.communicationScore());
        entity.setMobilityScore(dto.mobilityScore());
        entity.setMemoryScore(dto.memoryScore());
        entity.setMoodState(dto.moodState());
        entity.setDescription(dto.description());

        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);

        // Calcula dinamicamente a semana do mês (ex: 1ª, 2ª, 3ª ou 4ª semana)
        int weekOfMonth = now.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
        entity.setWeekOfMonth(weekOfMonth);

        WeeklyProgressEntity saved = weeklyProgressRepository.save(entity);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<WeeklyProgressResponseDTO> getGroupProgressHistory(UserEntity userLogado) {
        Long groupId = userLogado.getGroup().getId();
        return weeklyProgressRepository.findByGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void deleteProgress(Long id, UserEntity userLogado) {
        WeeklyProgressEntity entity = weeklyProgressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de progresso não encontrado"));

        // Validação de segurança: apenas membros do mesmo grupo podem excluir
        if (!entity.getGroup().getId().equals(userLogado.getGroup().getId())) {
            throw new RuntimeException("Acesso negado: registro pertence a outro grupo");
        }

        weeklyProgressRepository.delete(entity);
    }

    private WeeklyProgressResponseDTO mapToDTO(WeeklyProgressEntity entity) {
        return new WeeklyProgressResponseDTO(
                entity.getId(),
                entity.getCommunicationScore(),
                entity.getMobilityScore(),
                entity.getMemoryScore(),
                entity.getMoodState(),
                entity.getDescription(),
                entity.getWeekOfMonth(),
                entity.getCreatedAt(),
                entity.getUser().getName(), // Ou getUsername()
                entity.getGroup().getId()
        );
    }
}