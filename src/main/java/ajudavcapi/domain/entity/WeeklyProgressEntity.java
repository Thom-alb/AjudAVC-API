package ajudavcapi.domain.entity;

import ajudavcapi.domain.enums.MoodState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_progress")
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

    @Min(1) @Max(10)
    @Column(name = "communication_score", nullable = false)
    private Integer communicationScore;

    @Min(1) @Max(10)
    @Column(name = "mobility_score", nullable = false)
    private Integer mobilityScore;

    @Min(1) @Max(10)
    @Column(name = "memory_score", nullable = false)
    private Integer memoryScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_state", nullable = false)
    private MoodState moodState;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // NOVO CAMPO: Identifica a semana dentro do mês (1, 2, 3, 4 ou 5)
    @Column(name = "week_of_month", nullable = false)
    private Integer weekOfMonth;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters, Setters e Construtores
    public WeeklyProgressEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GroupEntity getGroup() { return group; }
    public void setGroup(GroupEntity group) { this.group = group; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public Integer getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Integer communicationScore) { this.communicationScore = communicationScore; }

    public Integer getMobilityScore() { return mobilityScore; }
    public void setMobilityScore(Integer mobilityScore) { this.mobilityScore = mobilityScore; }

    public Integer getMemoryScore() { return memoryScore; }
    public void setMemoryScore(Integer memoryScore) { this.memoryScore = memoryScore; }

    public MoodState getMoodState() { return moodState; }
    public void setMoodState(MoodState moodState) { this.moodState = moodState; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getWeekOfMonth() { return weekOfMonth; }
    public void setWeekOfMonth(Integer weekOfMonth) { this.weekOfMonth = weekOfMonth; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}