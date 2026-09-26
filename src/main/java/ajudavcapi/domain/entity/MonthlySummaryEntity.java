package ajudavcapi.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_summary", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "summary_month", "summary_year"})
})
public class MonthlySummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @Column(name = "summary_month", nullable = false)
    private Integer month; // 1 a 12

    @Column(name = "summary_year", nullable = false)
    private Integer year; // Ex: 2026

    @Column(name = "average_communication")
    private Double averageCommunication;

    @Column(name = "average_mobility")
    private Double averageMobility;

    @Column(name = "average_memory")
    private Double averageMemory;

    @Column(name = "count_animo", nullable = false)
    private Integer countAnimo = 0;

    @Column(name = "count_feliz", nullable = false)
    private Integer countFeliz = 0;

    @Column(name = "count_apatia", nullable = false)
    private Integer countApatia = 0;

    @Column(name = "count_raiva", nullable = false)
    private Integer countRaiva = 0;

    @Column(name = "count_triste", nullable = false)
    private Integer countTriste = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MonthlySummaryEntity() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GroupEntity getGroup() { return group; }
    public void setGroup(GroupEntity group) { this.group = group; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getAverageCommunication() { return averageCommunication; }
    public void setAverageCommunication(Double averageCommunication) { this.averageCommunication = averageCommunication; }

    public Double getAverageMobility() { return averageMobility; }
    public void setAverageMobility(Double averageMobility) { this.averageMobility = averageMobility; }

    public Double getAverageMemory() { return averageMemory; }
    public void setAverageMemory(Double averageMemory) { this.averageMemory = averageMemory; }

    public Integer getCountAnimo() { return countAnimo; }
    public void setCountAnimo(Integer countAnimo) { this.countAnimo = countAnimo; }

    public Integer getCountFeliz() { return countFeliz; }
    public void setCountFeliz(Integer countFeliz) { this.countFeliz = countFeliz; }

    public Integer getCountApatia() { return countApatia; }
    public void setCountApatia(Integer countApatia) { this.countApatia = countApatia; }

    public Integer getCountRaiva() { return countRaiva; }
    public void setCountRaiva(Integer countRaiva) { this.countRaiva = countRaiva; }

    public Integer getCountTriste() { return countTriste; }
    public void setCountTriste(Integer countTriste) { this.countTriste = countTriste; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}