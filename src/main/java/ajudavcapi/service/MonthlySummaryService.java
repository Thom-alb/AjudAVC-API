package ajudavcapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.MonthlySummaryEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.MonthlySummaryRepository;
import ajudavcapi.domain.dto.monthlySummary.CreateMonthlySummaryDTO;
import ajudavcapi.domain.dto.monthlySummary.MonthlySummaryResponseDTO;

@Service
public class MonthlySummaryService {

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public MonthlySummaryResponseDTO createOrUpdateSummary(CreateMonthlySummaryDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        // Se já existir um resumo para o mês/ano, atualiza-o
        MonthlySummaryEntity summary = monthlySummaryRepository
                .findByGroupIdAndMonthAndYear(group.getId(), dto.month(), dto.year())
                .orElse(new MonthlySummaryEntity());

        summary.setGroup(group);
        summary.setMonth(dto.month());
        summary.setYear(dto.year());
        summary.setAverageCommunication(dto.averageCommunication());
        summary.setAverageMobility(dto.averageMobility());
        summary.setAverageMemory(dto.averageMemory());

        summary.setCountAnimo(dto.countAnimo() != null ? dto.countAnimo() : 0);
        summary.setCountFeliz(dto.countFeliz() != null ? dto.countFeliz() : 0);
        summary.setCountApatia(dto.countApatia() != null ? dto.countApatia() : 0);
        summary.setCountRaiva(dto.countRaiva() != null ? dto.countRaiva() : 0);
        summary.setCountTriste(dto.countTriste() != null ? dto.countTriste() : 0);

        if (summary.getId() == null) {
            summary.setCreatedAt(LocalDateTime.now());
        }

        MonthlySummaryEntity savedSummary = monthlySummaryRepository.save(summary);

        return mapToDTO(savedSummary);
    }

    @Transactional(readOnly = true)
    public List<MonthlySummaryResponseDTO> getGroupSummaries(UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        return monthlySummaryRepository.findByGroupIdOrderByYearDescMonthDesc(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponseDTO getSummaryByMonthAndYear(Integer month, Integer year, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        MonthlySummaryEntity summary = monthlySummaryRepository
                .findByGroupIdAndMonthAndYear(group.getId(), month, year)
                .orElseThrow(() -> new IllegalArgumentException("Resumo do mês " + month + "/" + year + " não encontrado."));

        return mapToDTO(summary);
    }

    @Transactional
    public void deleteSummary(Long id, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        MonthlySummaryEntity summary = monthlySummaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resumo mensal não encontrado."));

        if (!summary.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Este resumo pertence a outro grupo.");
        }

        monthlySummaryRepository.delete(summary);
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