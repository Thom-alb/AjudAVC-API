package ajudavcapi.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ajudavcapi.domain.enums.StrokeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    // Enum para definição do tipo de avc
    @Enumerated(EnumType.STRING)
    @Column(name = "stroke_type", nullable = false)
    private StrokeType strokeType;

    // Data em que ocorreu o AVC (importante para calcular o tempo de recuperação)
    @Column(name = "stroke_date")
    private LocalDate strokeDate;

    @Column(name = "important_description", columnDefinition = "TEXT")
    private String importantDescription;

    // Ligação do paciente para o grupo, 1 paciente por 1 grupo
    @OneToOne(mappedBy = "patient")
    private GroupEntity group;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}