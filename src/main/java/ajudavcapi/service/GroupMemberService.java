package ajudavcapi.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.domain.repository.GroupMemberRepository;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.dto.groupmember.GroupMemberResponseDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberRoleDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberPermissionsDTO;

@Service
public class GroupMemberService {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ActivityLogService activityLogService;

    // Registra a associação do membro no grupo (usado ao criar ou entrar num grupo)
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

    // Listar todos os integrantes do grupo do usuário logado
    @Transactional(readOnly = true)
    public List<GroupMemberResponseDTO> getGroupMembers(UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);

        return groupMemberRepository.findByGroupId(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Alterar o papel/role de um integrante (Apenas o Líder do grupo pode fazer)
    @Transactional
    public GroupMemberResponseDTO updateMemberRole(Long memberId, UpdateGroupMemberRoleDTO dto, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        ensureLeader(group, userLogado);

        GroupMemberEntity member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membro do grupo não encontrado."));

        if (!member.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Este integrante pertence a outro grupo.");
        }

        member.setRole(dto.role());
        GroupMemberEntity updatedMember = groupMemberRepository.save(member);
        activityLogService.createSystemLog(
                userLogado, group, "Função atualizada: " + member.getUser().getName(),
                "A função do integrante foi alterada para " + dto.role() + ".",
                "ROLE_UPDATE", "AUDIT");

        return mapToDTO(updatedMember);
    }

    @Transactional
    public GroupMemberResponseDTO updateMemberPermissions(
            Long memberId,
            UpdateGroupMemberPermissionsDTO dto,
            UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        ensureLeader(group, userLogado);

        GroupMemberEntity member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membro do grupo não encontrado."));

        if (!member.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Este integrante pertence a outro grupo.");
        }
        if (member.getRole() == GroupRole.LEADER) {
            throw new IllegalArgumentException("As permissões do líder não podem ser alteradas por este endpoint.");
        }

        member.setCanEditRoutine(Boolean.TRUE.equals(dto.canEditRoutine()));
        member.setCanEditProgress(Boolean.TRUE.equals(dto.canEditProgress()));
        GroupMemberEntity updated = groupMemberRepository.save(member);
        activityLogService.createSystemLog(
                userLogado, group, "Permissões atualizadas: " + member.getUser().getName(),
                "Permissões de rotina=" + member.isCanEditRoutine()
                        + ", progresso=" + member.isCanEditProgress()
                        + " para " + member.getUser().getName() + ".",
                "PERMISSIONS", "AUDIT");
        return mapToDTO(updated);
    }

    private void ensureLeader(GroupEntity group, UserEntity user) {
        if (group.getLeader() == null || user == null || user.getId() == null
                || !group.getLeader().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Apenas o líder do grupo pode alterar permissões.");
        }
    }

    // Remover um integrante do grupo (Apenas o Líder ou o próprio membro saindo)
    @Transactional
    public void removeMember(Long memberId, UserEntity userLogado) {
        GroupEntity group = getUserGroup(userLogado);
        ensureLeader(group, userLogado);

        GroupMemberEntity member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membro do grupo não encontrado."));

        if (!member.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("Acesso negado: Este integrante pertence a outro grupo.");
        }

        // Evita que o Líder remova a si próprio deixando o grupo sem líder
        if (member.getUser().getId().equals(group.getLeader().getId())) {
            throw new IllegalArgumentException("O líder do grupo não pode ser removido diretamente.");
        }

        activityLogService.createSystemLog(
                userLogado, group, "Integrante removido: " + member.getUser().getName(),
                "O integrante foi removido do grupo de cuidado.",
                "MEMBER_REMOVAL", "AUDIT");
        groupMemberRepository.delete(member);
    }

    private GroupEntity getUserGroup(UserEntity user) {
        if (user.getGroup() != null) {
            return user.getGroup();
        }
        return groupRepository.findByLeaderIdOrMembersUserId(user.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui nenhum grupo associado."));
    }

    private GroupMemberResponseDTO mapToDTO(GroupMemberEntity entity) {
        return new GroupMemberResponseDTO(
            entity.getId(),
            entity.getUser().getId(),
            entity.getUser().getName(),
            entity.getUser().getEmail(),
            entity.getRole(),
            entity.getJoinedAt(),
            entity.isCanEditRoutine(),
            entity.isCanEditProgress()
        );
    }
}