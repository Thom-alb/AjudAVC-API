package ajudavcapi.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.UserEntity;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    List<GroupEntity> findByLeader(UserEntity leader);
}