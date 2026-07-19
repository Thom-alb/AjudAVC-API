package ajudavcapi.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.Group;
import ajudavcapi.domain.entity.User;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByLeader(User leader);
}