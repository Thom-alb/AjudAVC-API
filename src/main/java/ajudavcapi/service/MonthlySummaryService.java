package ajudavcapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.monthlySummary.MonthlySummaryResponseDTO;
import ajudavcapi.domain.entity.MonthlySummaryEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;
import ajudavcapi.domain.repository.MonthlySummaryRepository;
import ajudavcapi.domain.repository.WeeklyProgressRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlySummaryService {

    private final MonthlySummaryRepository monthlySummaryRepository;
    private final WeeklyProgressRepository weeklyProgressRepository;

    @Transactional
    public MonthlySummaryResponseDTO getOrCalculateSummary(Integer month, Integer year, UserEntity userLogado) {
        Long groupId = userLogado.getGroup().getId();

        List<WeeklyProgressEntity> progressList = weeklyProgressRepository.findByGroupIdAndMonthAndYear(groupId, month, year);

        if (progressList.isEmpty()) {
            return new MonthlySummaryResponseDTO(null, month, year, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, null, groupId);
        }

        double avgComm = progressList.stream().mapToInt(WeeklyProgressEntity::getCommunicationScore).average().orElse(0.0);
        double avgMob = progressList.stream().mapToInt(WeeklyProgressEntity::getMobilityScore).average().orElse(0.0);
        double avgMem = progressList.stream().mapToInt(WeeklyProgressEntity::getMemoryScore).average().orElse(0.0);

        int cAnimo = progressList.stream().mapToInt(p -> zeroIfNull(p.getCountAnimo())).sum();
        int cFeliz = progressList.stream().mapToInt(p -> zeroIfNull(p.getCountFeliz())).sum();
        int cApatia = progressList.stream().mapToInt(p -> zeroIfNull(p.getCountApatia())).sum();
        int cRaiva = progressList.stream().mapToInt(p -> zeroIfNull(p.getCountRaiva())).sum();
        int cTriste = progressList.stream().mapToInt(p -> zeroIfNull(p.getCountTriste())).sum();

        MonthlySummaryEntity summary = monthlySummaryRepository.findByGroupIdAndMonthAndYear(groupId, month, year)
                .orElseGet(() -> {
                    MonthlySummaryEntity newEntity = new MonthlySummaryEntity();
                    newEntity.setGroup(userLogado.getGroup());
                    newEntity.setMonth(month);
                    newEntity.setYear(year);
                    return newEntity;
                });

        summary.setAverageCommunication(avgComm);
        summary.setAverageMobility(avgMob);
        summary.setAverageMemory(avgMem);
        summary.setCountAnimo(cAnimo);
        summary.setCountFeliz(cFeliz);
        summary.setCountApatia(cApatia);
        summary.setCountRaiva(cRaiva);
        summary.setCountTriste(cTriste);

        MonthlySummaryEntity saved = monthlySummaryRepository.save(summary);

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<MonthlySummaryResponseDTO> getGroupSummaries(UserEntity userLogado) {
        Long groupId = userLogado.getGroup().getId();
        return monthlySummaryRepository.findByGroupIdOrderByYearDescMonthDesc(groupId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private int zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }

    private MonthlySummaryResponseDTO mapToDTO(MonthlySummaryEntity entity) {
        return new MonthlySummaryResponseDTO(
                entity.getId(),
                entity.getMonth(),
                entity.getYear(),
                entity.getAverageCommunication(),
                entity.getAverageMobility(),
                entity.getAverageMemory(),
                entity.getCountAnimo(),
                entity.getCountFeliz(),
                entity.getCountApatia(),
                entity.getCountRaiva(),
                entity.getCountTriste(),
                entity.getCreatedAt(),
                entity.getGroup().getId()
        );
    }
}
