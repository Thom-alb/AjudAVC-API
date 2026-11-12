package ajudavcapi.domain.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import ajudavcapi.domain.enums.GroupStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "care_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "invite_code", nullable = false, unique = true, length = 6)
    private String inviteCode;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patient_id", nullable = true, unique = true)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
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

    // Helper method para garantir o vínculo bidirecional com PatientEntity
    public void setPatient(PatientEntity patient) {
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
                .anyMatch(existing -> existing.getUser() != null
                        && member.getUser() != null
                        && existing.getUser().getId() != null
                        && existing.getUser().getId().equals(
                                member.getUser().getId()));

        if (!alreadyAdded) {
            members.add(member);
            member.setGroup(this);
        }
    }

    public void removeMember(GroupMemberEntity member) {
        if (member != null && members.remove(member)) {
            member.setGroup(null);
        }
    }

    public void setInviteCode(String inviteCode) {
        if (inviteCode != null) {
            this.inviteCode = inviteCode.toUpperCase().trim();
        } else {
            this.inviteCode = null;
        }
    }
}