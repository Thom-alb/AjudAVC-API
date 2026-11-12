
package ajudavcapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.groupmember.GroupMemberResponseDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberPermissionsDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberRoleDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.domain.enums.GroupStatus;
import ajudavcapi.domain.repository.GroupMemberRepository;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.UserRepository;
import ajudavcapi.exception.ConflictException;
import ajudavcapi.exception.GroupNotFoundException;

@Service
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public GroupMemberService(
            GroupMemberRepository groupMemberRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            ActivityLogService activityLogService) {

        this.groupMemberRepository = groupMemberRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public GroupMemberEntity addMemberToGroup(
            GroupEntity group,
            UserEntity user,
            GroupRole role) {

        if (group == null || group.getId() == null
                || user == null || user.getId() == null) {
            throw new IllegalArgumentException(
                    "Grupo e usuário devem estar identificados.");
        }

        if (role == null) {
            throw new IllegalArgumentException(
                    "O papel do integrante é obrigatório.");
        }

        if (role == GroupRole.LEADER) {
            throw new IllegalArgumentException(
                    "A liderança deve ser atribuída por meio da transferência específica.");
        }

        GroupEntity managedGroup = groupRepository.findById(group.getId())
                .orElseThrow(() -> new GroupNotFoundException(
                        "Grupo não encontrado."));

        ensureGroupActive(managedGroup);

        if (groupMemberRepository.existsByGroupIdAndUserId(
                managedGroup.getId(), user.getId())) {
            throw new ConflictException(
                    "O usuário já faz parte deste grupo.");
        }

        UserEntity managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado."));

        if (managedUser.getGroup() != null) {
            throw new ConflictException(
                    "O usuário já está associado a outro grupo.");
        }

        GroupMemberEntity member = new GroupMemberEntity();
        member.setUser(managedUser);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());

        managedGroup.addMember(member);
        managedUser.setGroup(managedGroup);

        return groupMemberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponseDTO> getGroupMembers(
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);

        return groupMemberRepository.findByGroupId(group.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public GroupMemberResponseDTO updateMemberRole(
            Long memberId,
            UpdateGroupMemberRoleDTO dto,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);
        ensureLeader(group, userLogado);

        GroupMemberEntity member = getMemberInGroup(memberId, group);

        if (member.getRole() == GroupRole.LEADER) {
            throw new ConflictException(
                    "Não é permitido alterar o papel do líder atual.");
        }

        if (dto.role() == null || dto.role() == GroupRole.LEADER) {
            throw new IllegalArgumentException(
                    "A transferência de liderança deve ser realizada por uma operação específica.");
        }

        member.setRole(dto.role());

        GroupMemberEntity updatedMember =
                groupMemberRepository.save(member);

        activityLogService.createSystemLog(
                userLogado,
                group,
                "Função atualizada: " + member.getUser().getName(),
                "A função do integrante foi alterada para "
                        + dto.role() + ".",
                "ROLE_UPDATE",
                "AUDIT");

        return mapToDTO(updatedMember);
    }

    @Transactional
    public GroupMemberResponseDTO transferLeadership(
            Long memberId,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);
        ensureLeader(group, userLogado);

        GroupMemberEntity newLeader = getMemberInGroup(memberId, group);

        if (newLeader.getUser().getId().equals(userLogado.getId())) {
            throw new ConflictException(
                    "Você já é o líder deste grupo.");
        }

        if (newLeader.getRole() == GroupRole.LEADER) {
            throw new ConflictException(
                    "Este integrante já é o líder do grupo.");
        }

        GroupMemberEntity currentLeader = groupMemberRepository
                .findByGroupIdAndUserId(group.getId(), userLogado.getId())
                .orElseThrow(() -> new ConflictException(
                        "O registro de liderança atual não foi encontrado."));

        if (currentLeader.getRole() != GroupRole.LEADER) {
            throw new ConflictException(
                    "O papel do líder atual está inconsistente.");
        }

        currentLeader.setRole(GroupRole.MEMBER);
        currentLeader.setCanEditRoutine(false);
        currentLeader.setCanEditProgress(false);

        newLeader.setRole(GroupRole.LEADER);
        newLeader.setCanEditRoutine(true);
        newLeader.setCanEditProgress(true);

        group.setLeader(newLeader.getUser());

        groupMemberRepository.saveAll(
                List.of(currentLeader, newLeader));

        groupRepository.save(group);

        activityLogService.createSystemLog(
                userLogado,
                group,
                "Liderança transferida: " + newLeader.getUser().getName(),
                "A liderança do grupo foi transferida de "
                        + userLogado.getName()
                        + " para "
                        + newLeader.getUser().getName()
                        + ".",
                "LEADERSHIP_TRANSFER",
                "AUDIT");

        return mapToDTO(newLeader);
    }

    @Transactional
    public GroupMemberResponseDTO updateMemberPermissions(
            Long memberId,
            UpdateGroupMemberPermissionsDTO dto,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);
        ensureLeader(group, userLogado);

        GroupMemberEntity member = getMemberInGroup(memberId, group);

        if (member.getRole() == GroupRole.LEADER) {
            throw new ConflictException(
                    "As permissões do líder não podem ser alteradas por este endpoint.");
        }

        member.setCanEditRoutine(
                Boolean.TRUE.equals(dto.canEditRoutine()));

        member.setCanEditProgress(
                Boolean.TRUE.equals(dto.canEditProgress()));

        GroupMemberEntity updated =
                groupMemberRepository.save(member);

        activityLogService.createSystemLog(
                userLogado,
                group,
                "Permissões atualizadas: " + member.getUser().getName(),
                "Permissões de rotina="
                        + member.isCanEditRoutine()
                        + ", progresso="
                        + member.isCanEditProgress()
                        + " para " + member.getUser().getName() + ".",
                "PERMISSIONS",
                "AUDIT");

        return mapToDTO(updated);
    }

    @Transactional
    public void removeMember(
            Long memberId,
            UserEntity userLogado) {

        GroupEntity group = getUserGroup(userLogado);
        ensureGroupActive(group);

        GroupMemberEntity member = getMemberInGroup(memberId, group);

        boolean isLeader = group.getLeader() != null
                && group.getLeader().getId().equals(userLogado.getId());

        boolean isSelf = member.getUser().getId()
                .equals(userLogado.getId());

        if (!isLeader && !isSelf) {
            throw new AccessDeniedException(
                    "Você não tem permissão para remover este integrante.");
        }

        boolean isOfficialLeader = group.getLeader() != null
                && group.getLeader().getId().equals(member.getUser().getId());

        if (isOfficialLeader || member.getRole() == GroupRole.LEADER) {
            throw new ConflictException(
                    "O líder não pode ser removido sem transferir a liderança.");
        }

        UserEntity managedUser = userRepository.findById(
                member.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário do grupo não encontrado."));

        activityLogService.createSystemLog(
                userLogado,
                group,
                "Integrante removido: " + managedUser.getName(),
                "O integrante saiu ou foi removido do grupo de cuidado.",
                "MEMBER_REMOVAL",
                "AUDIT");

        managedUser.setGroup(null);

        group.removeMember(member);
    }

    private void ensureGroupActive(GroupEntity group) {

        if (group == null
                || group.getStatus() == GroupStatus.ARCHIVED) {

            throw new ConflictException(
                    "Este grupo está arquivado e permite somente consultas.");
        }
    }

    private GroupEntity getUserGroup(UserEntity user) {

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException(
                    "Usuário autenticado não identificado.");
        }

        return groupRepository
                .findByLeaderIdOrMembersUserId(
                        user.getId(), user.getId())
                .orElseThrow(() -> new GroupNotFoundException(
                        "O usuário não pertence a nenhum grupo de cuidado."));
    }

    private GroupMemberEntity getMemberInGroup(
            Long memberId,
            GroupEntity group) {

        GroupMemberEntity member = groupMemberRepository
                .findById(memberId)
                .orElseThrow(() -> new GroupNotFoundException(
                        "Membro do grupo não encontrado."));

        if (member.getGroup() == null
                || !member.getGroup().getId().equals(group.getId())) {

            throw new AccessDeniedException(
                    "Este integrante pertence a outro grupo.");
        }

        return member;
    }

    private void ensureLeader(
            GroupEntity group,
            UserEntity user) {

        if (group.getLeader() == null
                || user == null
                || user.getId() == null
                || !group.getLeader().getId().equals(user.getId())) {

            throw new AccessDeniedException(
                    "Apenas o líder do grupo pode realizar esta operação.");
        }
    }

    private GroupMemberResponseDTO mapToDTO(
            GroupMemberEntity entity) {

        return new GroupMemberResponseDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getName(),
                entity.getUser().getEmail(),
                entity.getRole(),
                entity.getJoinedAt(),
                entity.isCanEditRoutine(),
                entity.isCanEditProgress());
    }
}
