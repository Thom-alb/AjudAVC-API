package ajudavcapi.domain.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ajudavcapi.domain.enums.MoodState;

@Entity
@Table(name = "weekly_progresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WeeklyProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O registro de progresso pertence a um grupo específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    // O cuidador/usuário que realizou esta avaliação de progresso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Sliders de avaliação de 1 a 10 demonstrados no layout da tela
    @Min(1) @Max(10)
    @Column(name = "communication_score", nullable = false)
    private Integer communicationScore;

    @Min(1) @Max(10)
    @Column(name = "mobility_score", nullable = false)
    private Integer mobilityScore;

    @Min(1) @Max(10)
    @Column(name = "memory_score", nullable = false)
    private Integer memoryScore;

    // Estado de Humor selecionado na interface gráfica
    @Enumerated(EnumType.STRING)
    @Column(name = "mood_state", nullable = false)
    private MoodState moodState;

    // Campo de texto livre para observações gerais ("Descrição/Digite aqui")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}