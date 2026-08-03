package ajudavcapi.domain.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.UserRepository;
import ajudavcapi.dto.CreateGroupDTO;
import ajudavcapi.dto.GroupResponseDTO;
import ajudavcapi.dto.JoinGroupDTO;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public GroupResponseDTO createGroup(CreateGroupDTO dto, UserEntity userLogado) {
        var existingGroups = groupRepository.findByLeader(userLogado);
        if (!existingGroups.isEmpty()) {
            throw new IllegalArgumentException("O usuário já possui um grupo cadastrado.");
        }

        GroupEntity group = new GroupEntity();
        group.setName(dto.name());
        group.setLeader(userLogado);
        group.setInviteCode(generateUniqueInviteCode());

        // Adiciona o líder na lista de membros do grupo com a role LEADER
        GroupMemberEntity leaderMember = new GroupMemberEntity();
        leaderMember.setGroup(group);
        leaderMember.setUser(userLogado);
        leaderMember.setRole(GroupRole.LEADER);
        group.getMembers().add(leaderMember);

        // Atualiza a role do UserEntity
        if (userLogado.getRole() != GroupRole.LEADER) {
            userLogado.setRole(GroupRole.LEADER);
            userRepository.save(userLogado);
        }

        GroupEntity savedGroup = groupRepository.save(group);

        return new GroupResponseDTO(
            savedGroup.getId(),
            savedGroup.getName(),
            savedGroup.getInviteCode(),
            savedGroup.getLeader().getId()
        );
    }

    @Transactional
    public GroupResponseDTO joinGroup(JoinGroupDTO dto, UserEntity userLogado) {
        GroupEntity group = groupRepository.findByInviteCode(dto.inviteCode())
                .orElseThrow(() -> new IllegalArgumentException("Código de convite inválido ou não encontrado."));

        // Adiciona novo membro
        GroupMemberEntity member = new GroupMemberEntity();
        member.setGroup(group);
        member.setUser(userLogado);
        member.setRole(GroupRole.MEMBER);
        group.getMembers().add(member);

        userLogado.setRole(GroupRole.MEMBER);
        userRepository.save(userLogado);

        GroupEntity savedGroup = groupRepository.save(group);

        return new GroupResponseDTO(
            savedGroup.getId(),
            savedGroup.getName(),
            savedGroup.getInviteCode(),
            savedGroup.getLeader().getId()
        );
    }

    private String generateUniqueInviteCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}