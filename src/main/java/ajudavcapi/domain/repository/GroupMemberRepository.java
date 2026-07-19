package ajudavcapi.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.Group;
import ajudavcapi.domain.entity.GroupMember;
import ajudavcapi.domain.entity.User;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroup(Group group);
    List<GroupMember> findByUser(User user);
    boolean existsByGroupAndUser(Group group, User user);
}