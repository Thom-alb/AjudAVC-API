package ajudavcapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.groupmember.GroupMemberResponseDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberRoleDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.domain.repository.GroupMemberRepository;
import ajudavcapi.domain.repository.GroupRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public GroupMemberEntity addMemberToGroup(GroupEntity group, UserEntity user, GroupRole role) {
        if (groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            throw new IllegalArgumentException("O usuário já faz parte deste grupo.");
        }

        GroupMemberEntity member = new GroupMemberEntity();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());

        return groupMemberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponseDTO> getGroupMembers(UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        return groupMemberRepository.findByGroupId(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public GroupMemberResponseDTO updateMemberRole(Long memberId, UpdateGroupMemberRoleDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        GroupMemberEntity member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membro do grupo não encontrado."));

        if (!member.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Este integrante pertence a outro grupo.");
        }

        member.setRole(dto.role());
        GroupMemberEntity updatedMember = groupMemberRepository.save(member);

        return mapToDTO(updatedMember);
    }

    @Transactional
    public void removeMember(Long memberId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        GroupMemberEntity member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membro do grupo não encontrado."));

        if (!member.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Este integrante pertence a outro grupo.");
        }

        if (member.getUser().getId().equals(group.getLeader().getId())) {
            throw new IllegalArgumentException("O líder do grupo não pode ser removido diretamente.");
        }

        groupMemberRepository.delete(member);
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user.getGroup() != null) {
            return user.getGroup();
        }
        return groupRepository.findByLeader(user)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui nenhum grupo associado."));
    }

    private GroupMemberResponseDTO mapToDTO(GroupMemberEntity entity) {
        return new GroupMemberResponseDTO(
            entity.getId(),
            entity.getUser().getId(),
            entity.getUser().getName(),
            entity.getUser().getEmail(),
            entity.getRole(),
            entity.getJoinedAt()
        );
    }
}