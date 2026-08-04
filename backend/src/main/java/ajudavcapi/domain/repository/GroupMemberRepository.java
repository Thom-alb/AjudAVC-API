package ajudavcapi.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ajudavcapi.domain.entity.GroupMemberEntity;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, Long> {

    // Lista todos os membros de um grupo específico
    List<GroupMemberEntity> findByGroupId(Long groupId);

    // Busca o vínculo do usuário em um grupo
    Optional<GroupMemberEntity> findByGroupIdAndUserId(Long groupId, Long userId);

    // Verifica se um usuário já está cadastrado em um determinado grupo
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}