package ajudavcapi.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;

import ajudavcapi.domain.enums.GroupStatus;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "care_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "invite_code", nullable = false, unique = true, length = 6)
    private String inviteCode;

    /*
     * Este lado é o DONO da FK (care_groups.patient_id).
     * Ao persistir o grupo, o Hibernate persiste o paciente ANTES (para obter o id
     * da FK) e, em cascata, seus AVCs e doenças.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patient_id", unique = true)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leader_id", nullable = false)
    private UserEntity leader;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMemberEntity> members = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GroupStatus status = GroupStatus.ACTIVE;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    public void archive() {
        this.status = GroupStatus.ARCHIVED;
        this.archivedAt = LocalDateTime.now();
    }

    /** Mantém os dois lados do OneToOne sincronizados. */
    public void setPatient(PatientEntity patient) {
        if (this.patient != null && this.patient != patient) {
            this.patient.setGroup(null);
        }

        this.patient = patient;

        if (patient != null) {
            patient.setGroup(this);
        }
    }

    public void addMember(GroupMemberEntity member) {
        if (member == null) {
            return;
        }

        boolean alreadyAdded = members.stream()
                .anyMatch(existing -> existing == member
                        || (existing.getUser() != null
                                && member.getUser() != null
                                && existing.getUser().getId() != null
                                && existing.getUser().getId().equals(member.getUser().getId())));

        if (!alreadyAdded) {
            members.add(member);
            member.setGroup(this);
        }
    }

    public void removeMember(GroupMemberEntity member) {
        if (member == null) {
            return;
        }

        if (members.removeIf(existing -> existing == member)) {
            member.setGroup(null);
        }
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode != null ? inviteCode.toUpperCase().trim() : null;
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
        return id != null && id.equals(((GroupEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
