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
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A atividade acontece dentro do contexto de um Grupo específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // O usuário/cuidador que criou ou realizou a atividade
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title; // Ex: "Medicação - Losartana 50mg", "Banho de Leito"

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Detalhes completos para a consulta do CRUD

    // Data e hora planejada ou em que a atividade ocorreu (usada para plotar no calendário)
    @Column(name = "activity_date", nullable = false)
    private LocalDateTime activityDate;

    // Status do fluxo de controle (Ex: "PENDING", "COMPLETED", "CANCELED")
    @Column(name = "status", nullable = false)
    private String status = "PENDING"; 

    @Column(name = "category")
    private String category; // Ex: "MEDICATION", "HYGIENE", "FEEDING", "THERAPY"

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}