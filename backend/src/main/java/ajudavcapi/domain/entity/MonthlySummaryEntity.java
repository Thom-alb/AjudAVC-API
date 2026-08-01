package ajudavcapi.domain.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "monthly_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class MonthlySummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O resumo pertence a um grupo/paciente específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    // Identificadores do período do resumo
    @Column(name = "summary_month", nullable = false)
    private Integer month; // Ex: 7 para Julho

    @Column(name = "summary_year", nullable = false)
    private Integer year;  // Ex: 2026

    // Médias aritméticas dos sliders calculadas no período
    @Column(name = "average_communication")
    private Double averageCommunication;

    @Column(name = "average_mobility")
    private Double averageMobility;

    @Column(name = "average_memory")
    private Double averageMemory;

    // Contadores de Humor (Variáveis que acumulam a frequência dos Enums)
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
}