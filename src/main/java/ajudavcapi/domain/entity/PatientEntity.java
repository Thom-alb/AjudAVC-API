package ajudavcapi.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    /*
     * Lado "dono" da FK é StrokeEntity.patient / DiseaseEntity.patient.
     * cascade ALL + orphanRemoval: salvar o paciente salva AVCs e doenças;
     * remover da lista apaga a linha.
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StrokeEntity> strokes = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiseaseEntity> diseases = new ArrayList<>();

    /*
     * Lado INVERSO do vínculo (a coluna care_groups.patient_id pertence ao
     * GroupEntity). Nunca é gravado por aqui; GroupEntity#setPatient mantém os
     * dois lados sincronizados.
     */
    @JsonIgnore
    @OneToOne(mappedBy = "patient", fetch = FetchType.LAZY)
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

        // Comparação por identidade: instâncias novas (id == null) são distintas.
        boolean alreadyAdded = strokes.stream().anyMatch(existing -> existing == stroke);

        if (!alreadyAdded) {
            strokes.add(stroke);
        }

        stroke.setPatient(this);
    }

    public void removeStroke(StrokeEntity stroke) {
        if (stroke == null) {
            return;
        }

        // Com orphanRemoval, basta retirar da coleção; não zeramos patient
        // (a coluna é NOT NULL e a entidade será excluída no flush).
        strokes.removeIf(existing -> existing == stroke);
    }

    public void addDisease(DiseaseEntity disease) {
        if (disease == null) {
            return;
        }

        if (disease.getPatient() != null && disease.getPatient() != this) {
            throw new IllegalArgumentException(
                    "Esta doença já está associada a outro paciente.");
        }

        boolean alreadyAdded = diseases.stream().anyMatch(existing -> existing == disease);

        if (!alreadyAdded) {
            diseases.add(disease);
        }

        disease.setPatient(this);
    }

    public void removeDisease(DiseaseEntity disease) {
        if (disease == null) {
            return;
        }

        diseases.removeIf(existing -> existing == disease);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> thisClass = Hibernate.getClass(this);
        Class<?> otherClass = Hibernate.getClass(o);
        if (thisClass != otherClass) {
            return false;
        }
        return id != null && id.equals(((PatientEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
