package ajudavcapi.service;

import ajudavcapi.domain.dto.monthlySummary.MonthlySummaryResponseDTO;
import ajudavcapi.domain.entity.MonthlySummaryEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;
import ajudavcapi.domain.enums.MoodState;
import ajudavcapi.domain.repository.MonthlySummaryRepository;
import ajudavcapi.domain.repository.WeeklyProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MonthlySummaryService {

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    @Autowired
    private WeeklyProgressRepository weeklyProgressRepository;

    @Transactional
    public MonthlySummaryResponseDTO getOrCalculateSummary(Integer month, Integer year, UserEntity userLogado) {
        Long groupId = userLogado.getGroup().getId();

        // 1. Busca todas as avaliações registradas naquele mês para o grupo
        List<WeeklyProgressEntity> progressList = weeklyProgressRepository.findByGroupIdAndMonthAndYear(groupId, month, year);

        // 2. Se não houver registros no mês, retorna valores zerados
        if (progressList.isEmpty()) {
            return new MonthlySummaryResponseDTO(null, month, year, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, null, groupId);
        }

        // 3. Calcula as médias dos sliders
        double avgComm = progressList.stream().mapToInt(WeeklyProgressEntity::getCommunicationScore).average().orElse(0.0);
        double avgMob = progressList.stream().mapToInt(WeeklyProgressEntity::getMobilityScore).average().orElse(0.0);
        double avgMem = progressList.stream().mapToInt(WeeklyProgressEntity::getMemoryScore).average().orElse(0.0);

        // 4. Conta as ocorrências de cada humor no mês
        int cAnimo = (int) progressList.stream().filter(p -> p.getMoodState() == MoodState.ANIMO).count();
        int cFeliz = (int) progressList.stream().filter(p -> p.getMoodState() == MoodState.FELIZ).count();
        int cApatia = (int) progressList.stream().filter(p -> p.getMoodState() == MoodState.APATIA).count();
        int cRaiva = (int) progressList.stream().filter(p -> p.getMoodState() == MoodState.RAIVA).count();
        int cTriste = (int) progressList.stream().filter(p -> p.getMoodState() == MoodState.TRISTE).count();

        // 5. Atualiza ou cria o registro no banco para cache/histórico
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