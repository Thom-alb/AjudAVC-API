package ajudavcapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.monthlySummary.MonthlySummaryResponseDTO;
import ajudavcapi.domain.entity.MonthlySummaryEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.WeeklyProgressEntity;
import ajudavcapi.domain.repository.MonthlySummaryRepository;
import ajudavcapi.domain.repository.WeeklyProgressRepository;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.repository.GroupRepository;

@Service
public class MonthlySummaryService {

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    @Autowired
    private WeeklyProgressRepository weeklyProgressRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public MonthlySummaryResponseDTO getOrCalculateSummary(Integer month, Integer year, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        Long groupId = group.getId();

        List<WeeklyProgressEntity> progressList = weeklyProgressRepository.findByGroupIdAndMonthAndYear(groupId, month,
                year);

        if (progressList.isEmpty()) {
            return new MonthlySummaryResponseDTO(null, month, year, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, null, groupId);
        }

        double avgComm = progressList.stream()
                .mapToInt(p -> p.getCommunicationScore() != null ? p.getCommunicationScore() : 0).average().orElse(0.0);
        double avgMob = progressList.stream().mapToInt(p -> p.getMobilityScore() != null ? p.getMobilityScore() : 0)
                .average().orElse(0.0);
        double avgMem = progressList.stream().mapToInt(p -> p.getMemoryScore() != null ? p.getMemoryScore() : 0)
                .average().orElse(0.0);

        int cAnimo = progressList.stream().mapToInt(p -> p.getCountAnimo() == null ? 0 : p.getCountAnimo()).sum();
        int cFeliz = progressList.stream().mapToInt(p -> p.getCountFeliz() == null ? 0 : p.getCountFeliz()).sum();
        int cApatia = progressList.stream().mapToInt(p -> p.getCountApatia() == null ? 0 : p.getCountApatia()).sum();
        int cRaiva = progressList.stream().mapToInt(p -> p.getCountRaiva() == null ? 0 : p.getCountRaiva()).sum();
        int cTriste = progressList.stream().mapToInt(p -> p.getCountTriste() == null ? 0 : p.getCountTriste()).sum();

        MonthlySummaryEntity summary = monthlySummaryRepository.findByGroupIdAndMonthAndYear(groupId, month, year)
                .orElseGet(() -> {
                    MonthlySummaryEntity newEntity = new MonthlySummaryEntity();
                    newEntity.setGroup(group);
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
        Long groupId = getUserGroup(userLogado).getId();
        return monthlySummaryRepository.findByGroupIdOrderByYearDescMonthDesc(groupId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário autenticado não encontrado.");
        }
        if (user.getGroup() != null) {
            return user.getGroup();
        }
        return groupRepository.findByLeaderIdOrMembersUserId(user.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui nenhum grupo associado."));
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
                entity.getGroup().getId());
    }
}