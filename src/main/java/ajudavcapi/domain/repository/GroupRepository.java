package ajudavcapi.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    List<GroupEntity> findByLeader(UserEntity leader);

    @Query("SELECT DISTINCT g FROM GroupEntity g LEFT JOIN FETCH g.patient LEFT JOIN FETCH g.leader WHERE g.inviteCode = :inviteCode")
    Optional<GroupEntity> findByInviteCode(@Param("inviteCode") String inviteCode);

    boolean existsByInviteCode(String inviteCode);

    @Query("SELECT DISTINCT g FROM GroupEntity g LEFT JOIN FETCH g.patient LEFT JOIN FETCH g.leader LEFT JOIN FETCH g.members m WHERE g.leader.id = :leaderId OR m.user.id = :userId")
    Optional<GroupEntity> findByLeaderIdOrMembersUserId(@Param("leaderId") Long leaderId, @Param("userId") Long userId);

}