package ajudavcapi.domain.entity;

import java.time.LocalDateTime;

import ajudavcapi.domain.enums.StrokeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "stroke_type", nullable = false)
    private StrokeType strokeType;

    @Column(name = "medical_condition", columnDefinition = "TEXT")
    private String medicalCondition; 

    @OneToOne(mappedBy = "patient")
    private User user;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}