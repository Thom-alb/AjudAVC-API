package ajudavcapi.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.group.CreateGroupDTO;
import ajudavcapi.domain.dto.group.GroupResponseDTO;
import ajudavcapi.domain.dto.group.JoinGroupDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.UserRepository;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public GroupResponseDTO createGroup(CreateGroupDTO dto, UserEntity userLogado) {
        var existingGroups = groupRepository.findByLeader(userLogado);
        if (!existingGroups.isEmpty() || userLogado.getGroup() != null) {
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

        // Atualiza o grupo e a role do UserEntity
        userLogado.setGroup(group);
        if (userLogado.getRole() != GroupRole.LEADER) {
            userLogado.setRole(GroupRole.LEADER);
        }
        userRepository.save(userLogado);

        GroupEntity savedGroup = groupRepository.save(group);

        return mapToDTO(savedGroup);
    }

    @Transactional(readOnly = true)
    public GroupResponseDTO getMyGroup(UserEntity user) {
        GroupEntity group = user.getGroup();
        if (group == null) {
            group = groupRepository.findByLeader(user)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não está vinculado a nenhum grupo."));
        }
        return mapToDTO(group);
    }

    @Transactional
    public GroupResponseDTO joinGroup(JoinGroupDTO dto, UserEntity userLogado) {
        if (dto.inviteCode() == null || dto.inviteCode().isBlank()) {
            throw new IllegalArgumentException("O código de convite é obrigatório.");
        }

        String cleanedCode = dto.inviteCode().trim().toUpperCase();

        GroupEntity group = groupRepository.findByInviteCode(cleanedCode)
                .orElseThrow(() -> new IllegalArgumentException("Código de convite inválido ou não encontrado."));

        // Valida se o usuário já está no grupo (seja por atributo direto ou na lista de membros)
        boolean isAlreadyMember = group.getMembers()
                .stream()
                .anyMatch(member -> member.getUser().getId().equals(userLogado.getId()));

        if (isAlreadyMember || (userLogado.getGroup() != null && userLogado.getGroup().getId().equals(group.getId()))) {
            throw new IllegalArgumentException("Você já faz parte deste grupo.");
        }

        // Adiciona novo membro na coleção
        GroupMemberEntity member = new GroupMemberEntity();
        member.setGroup(group);
        member.setUser(userLogado);
        member.setRole(GroupRole.MEMBER);
        group.getMembers().add(member);

        // Atualiza a referência no usuário
        userLogado.setGroup(group);
        userLogado.setRole(GroupRole.MEMBER);
        userRepository.save(userLogado);

        GroupEntity savedGroup = groupRepository.save(group);

        return mapToDTO(savedGroup);
    }

    // Garante que o código de 6 caracteres gerado não colida com outro grupo existente no banco
    private String generateUniqueInviteCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (groupRepository.findByInviteCode(code).isPresent());
        return code;
    }

    private GroupResponseDTO mapToDTO(GroupEntity entity) {
        return new GroupResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getInviteCode(),
            entity.getLeader().getId(),
            entity.getLeader().getName()
        );
    }
}