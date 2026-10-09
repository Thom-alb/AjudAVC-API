package ajudavcapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
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
        ensureMemberOfGroup(group, userLogado);
        return groupMemberRepository.findByGroupId(group.getId()).stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public GroupMemberResponseDTO updateMemberRole(Long memberId, UpdateGroupMemberRoleDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        ensureLeader(group, userLogado);

        GroupMemberEntity member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membro do grupo não encontrado."));
        ensureSameGroup(group, member);

        if (member.getUser().getId().equals(group.getLeader().getId()) && dto.role() != GroupRole.LEADER) {
            throw new IllegalArgumentException("A função do líder principal não pode ser removida.");
        }

        member.setRole(dto.role());
        return mapToDTO(groupMemberRepository.save(member));
    }

    @Transactional
    public void removeMember(Long memberId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        ensureLeader(group, userLogado);

        GroupMemberEntity member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membro do grupo não encontrado."));
        ensureSameGroup(group, member);

        if (member.getUser().getId().equals(group.getLeader().getId())) {
            throw new IllegalArgumentException("O líder principal do grupo não pode ser removido.");
        }
        groupMemberRepository.delete(member);
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user.getGroup() != null) return user.getGroup();
        return groupRepository.findByLeader(user).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui nenhum grupo associado."));
    }

    private void ensureLeader(GroupEntity group, UserEntity user) {
        if (group.getLeader() == null || !group.getLeader().getId().equals(user.getId())) {
            throw new AccessDeniedException("Somente o líder principal do grupo pode gerenciar integrantes e permissões.");
        }
    }

    private void ensureMemberOfGroup(GroupEntity group, UserEntity user) {
        boolean isLeader = group.getLeader() != null && group.getLeader().getId().equals(user.getId());
        boolean isMember = groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId());
        if (!isLeader && !isMember) {
            throw new AccessDeniedException("Você não pertence a este grupo.");
        }
    }

    private void ensureSameGroup(GroupEntity group, GroupMemberEntity member) {
        if (member.getGroup() == null || !member.getGroup().getId().equals(group.getId())) {
            throw new AccessDeniedException("Acesso negado: este integrante pertence a outro grupo.");
        }
    }

    private GroupMemberResponseDTO mapToDTO(GroupMemberEntity entity) {
        return new GroupMemberResponseDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getName(),
                entity.getUser().getEmail(),
                entity.getRole(),
                entity.getJoinedAt());
    }
}
