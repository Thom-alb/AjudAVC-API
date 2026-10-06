package ajudavcapi.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;


public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    List<GroupEntity> findByLeader(UserEntity leader);
    Optional<GroupEntity> findByInviteCode(String inviteCode);
    boolean existsByInviteCode(String inviteCode);
    Optional<GroupEntity> findByLeaderIdOrMembersUserId(Long leaderId, Long userId);

}