package ajudavcapi.domain.mapper;

import org.springframework.stereotype.Component;
import ajudavcapi.domain.dto.weeklyProgress.WeeklyProgressResponseDTO;
import ajudavcapi.domain.entity.WeeklyProgressEntity;

@Component
public class WeeklyProgressMapper {

    public WeeklyProgressResponseDTO toResponseDTO(WeeklyProgressEntity entity, Integer planned, Integer completed) {
        if (entity == null) {
            return null;
        }
        
        return new WeeklyProgressResponseDTO(
                entity.getId(),
                entity.getCommunicationScore(),
                entity.getMobilityScore(),
                entity.getMemoryScore(),
                entity.getCountAnimo(),
                entity.getCountFeliz() != null ? entity.getCountFeliz() : 0,
                entity.getCountApatia(),
                entity.getCountRaiva(),
                entity.getCountTriste(),
                entity.getDescription(),
                entity.getWeekOfMonth(),
                entity.getCreatedAt(),
                entity.getUser() != null ? entity.getUser().getName() : "Cuidador",
                entity.getGroup() != null ? entity.getGroup().getId() : null,
                planned,
                completed
        );
    }
}
