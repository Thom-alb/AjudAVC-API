package ajudavcapi.domain.entity;

import java.time.LocalDate;

import org.hibernate.Hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ajudavcapi.domain.enums.StrokeType;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "strokes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StrokeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "stroke_type", nullable = false)
    private StrokeType strokeType;

    @Column(name = "stroke_date", nullable = false)
    private LocalDate strokeDate;

    // LAZY evita carregar o paciente (e, por inverso, o grupo) a cada AVC lido.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    /*
     * equals/hashCode baseados SOMENTE em id persistido.
     * O @EqualsAndHashCode(of = "id") do Lombok considera duas instâncias novas
     * (id == null) como iguais, o que quebra List.remove/contains/Set com entidades
     * ainda não salvas.
     */
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
        return id != null && id.equals(((StrokeEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
