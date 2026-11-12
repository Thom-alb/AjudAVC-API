package ajudavcapi.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
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

    @Column(name = "important_description", columnDefinition = "TEXT")
    private String importantDescription;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StrokeEntity> strokes = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DiseaseEntity> diseases = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "patient")
    private GroupEntity group;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void addStroke(StrokeEntity stroke) {
        if (stroke == null) {
            return;
        }

        if (stroke.getPatient() != null && stroke.getPatient() != this) {
            throw new IllegalArgumentException(
                    "Este AVC já está associado a outro paciente.");
        }

        boolean alreadyAdded = strokes.stream().anyMatch(existing -> existing == stroke ||
                (existing.getId() != null &&
                        existing.getId().equals(stroke.getId())));

        if (!alreadyAdded) {
            strokes.add(stroke);
            stroke.setPatient(this);
        }
    }

    public void removeStroke(StrokeEntity stroke) {
        if (stroke == null) {
            return;
        }

        boolean removed = strokes.removeIf(existing -> existing == stroke ||
                (stroke.getId() != null &&
                        stroke.getId().equals(existing.getId())));

        if (removed) {
            stroke.setPatient(null);
        }
    }

    public void addDisease(DiseaseEntity disease) {
        if (disease == null) {
            return;
        }

        if (disease.getPatient() != null && disease.getPatient() != this) {
            throw new IllegalArgumentException(
                    "Esta doença já está associada a outro paciente.");
        }

        boolean alreadyAdded = diseases.stream().anyMatch(existing -> existing == disease ||
                (existing.getId() != null &&
                        existing.getId().equals(disease.getId())));

        if (!alreadyAdded) {
            diseases.add(disease);
            disease.setPatient(this);
        }
    }

    public void removeDisease(DiseaseEntity disease) {
        if (disease == null) {
            return;
        }

        boolean removed = diseases.removeIf(existing -> existing == disease ||
                (disease.getId() != null &&
                        disease.getId().equals(existing.getId())));

        if (removed) {
            disease.setPatient(null);
        }
    }

}