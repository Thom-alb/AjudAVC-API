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
@Table(name = "weekly_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WeeklyProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "communication_score", nullable = false)
    private Integer communicationScore;

    @Column(name = "mobility_score", nullable = false)
    private Integer mobilityScore;

    @Column(name = "memory_score", nullable = false)
    private Integer memoryScore;

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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "week_of_month", nullable = false)
    private Integer weekOfMonth;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
