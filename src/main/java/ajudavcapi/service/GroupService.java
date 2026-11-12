package ajudavcapi.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.group.CreateGroupDTO;
import ajudavcapi.domain.dto.group.GroupResponseDTO;
import ajudavcapi.domain.dto.group.JoinGroupDTO;
import ajudavcapi.domain.dto.group.UpdateGroupDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.entity.PatientEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.domain.mapper.PatientMapper;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.UserRepository;
import ajudavcapi.exception.ConflictException;
import ajudavcapi.exception.GroupNotFoundException;

@Service
public class GroupService {

        private final GroupRepository groupRepository;
        private final UserRepository userRepository;

        private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        private static final SecureRandom RANDOM = new SecureRandom();

        public GroupService(
                        GroupRepository groupRepository,
                        UserRepository userRepository) {
                this.groupRepository = groupRepository;
                this.userRepository = userRepository;
        }

        /**
         * Cria o grupo e, opcionalmente, o paciente (com AVCs e doenças) numa única
         * transação.
         *
         * Ordem deliberada:
         * 1) validar e GERAR o código de convite (faz query) ANTES de alterar qualquer
         * entidade gerenciada. Antes, o líder já apontava para um grupo ainda
         * transiente quando a query rodava, o que expõe o auto-flush a
         * "object references an unsaved transient instance".
         * 2) montar o grafo em memória (grupo -> paciente -> AVCs/doenças; grupo ->
         * membro líder).
         * 3) um único saveAndFlush(group): o cascade persiste paciente, AVCs, doenças
         * e membro, na ordem correta das FKs.
         * 4) SOMENTE depois vincular users.group_id (grupo já persistido).
         */
        @Transactional
        public GroupResponseDTO createGroup(
                        CreateGroupDTO dto,
                        UserEntity leader) {

                if (leader == null || leader.getId() == null) {
                        throw new IllegalArgumentException("Usuário autenticado não identificado.");
                }

                UserEntity managedLeader = userRepository.findById(leader.getId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Líder não encontrado."));

                if (managedLeader.getGroup() != null) {
                        throw new ConflictException(
                                        "O usuário já está associado a um grupo.");
                }

                String inviteCode = generateInviteCode(); // (1) query antes de mutar entidades

                GroupEntity group = new GroupEntity();
                group.setName(dto.name().trim());
                group.setLeader(managedLeader);
                group.setInviteCode(inviteCode);

                if (dto.patient() != null) { // (2)
                        PatientEntity patient = PatientMapper.toEntity(dto.patient());
                        group.setPatient(patient); // sincroniza os dois lados do OneToOne
                }

                GroupMemberEntity leaderMember = new GroupMemberEntity();
                leaderMember.setUser(managedLeader);
                leaderMember.setRole(GroupRole.LEADER);
                group.addMember(leaderMember);

                GroupEntity savedGroup = groupRepository.saveAndFlush(group); // (3)

                managedLeader.setGroup(savedGroup); // (4) dirty-checking grava users.group_id no commit

                return new GroupResponseDTO(savedGroup);
        }

        @Transactional
        public GroupResponseDTO updateGroup(
                        Long id,
                        UpdateGroupDTO dto,
                        UserEntity user) {

                if (user == null || user.getId() == null) {
                        throw new IllegalArgumentException(
                                        "Usuário autenticado não identificado.");
                }

                if (dto == null || dto.name() == null || dto.name().isBlank()) {
                        throw new IllegalArgumentException(
                                        "O nome do grupo é obrigatório.");
                }

                GroupEntity group = groupRepository.findById(id)
                                .orElseThrow(() -> new GroupNotFoundException(
                                                "Grupo não encontrado para o ID especificado."));

                if (group.getLeader() == null
                                || !group.getLeader().getId().equals(user.getId())) {

                        throw new org.springframework.security.access.AccessDeniedException(
                                        "Apenas o líder pode alterar o nome do grupo.");
                }

                group.setName(dto.name().trim());

                // Entidade já gerenciada: o UPDATE sai no commit; não é necessário save().
                return new GroupResponseDTO(group);
        }

        @Transactional
        public GroupResponseDTO joinGroup(
                        JoinGroupDTO dto,
                        UserEntity user) {

                if (user == null || user.getId() == null) {
                        throw new IllegalArgumentException(
                                        "Usuário autenticado não identificado.");
                }

                UserEntity managedUser = userRepository.findById(user.getId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Usuário não encontrado."));

                if (managedUser.getGroup() != null) {
                        throw new IllegalArgumentException(
                                        "O usuário já está associado a um grupo.");
                }

                String inviteCode = dto.inviteCode().trim().toUpperCase();

                GroupEntity group = groupRepository
                                .findByInviteCode(inviteCode)
                                .orElseThrow(() -> new GroupNotFoundException(
                                                "Código de convite inválido ou grupo não encontrado."));

                boolean alreadyMember = group.getMembers().stream()
                                .anyMatch(member -> member.getUser().getId().equals(managedUser.getId()));

                if (alreadyMember) {
                        throw new IllegalArgumentException(
                                        "O usuário já faz parte deste grupo.");
                }

                GroupMemberEntity newMember = new GroupMemberEntity();
                newMember.setUser(managedUser);
                newMember.setRole(GroupRole.MEMBER);

                group.addMember(newMember);

                // group já é gerenciado: o flush no commit persiste o membro (cascade) e o
                // UPDATE de users.group_id. Sem save() redundante (que faria merge).
                managedUser.setGroup(group);

                return new GroupResponseDTO(group);
        }

        @Transactional(readOnly = true)
        public GroupResponseDTO getMyGroup(UserEntity user) {

                if (user == null || user.getId() == null) {
                        throw new IllegalArgumentException(
                                        "Usuário autenticado não identificado.");
                }

                GroupEntity group = groupRepository
                                .findByLeaderIdOrMembersUserId(
                                                user.getId(),
                                                user.getId())
                                .orElseThrow(() -> new GroupNotFoundException(
                                                "O usuário não pertence a nenhum grupo de cuidado."));

                return new GroupResponseDTO(group);
        }

        @Transactional(readOnly = true)
        public GroupResponseDTO getGroupById(
                        Long id,
                        UserEntity user) {

                if (user == null || user.getId() == null) {
                        throw new IllegalArgumentException(
                                        "Usuário autenticado não identificado.");
                }

                GroupEntity group = groupRepository.findById(id)
                                .orElseThrow(() -> new GroupNotFoundException(
                                                "Grupo não encontrado para o ID especificado."));

                boolean isLeader = group.getLeader() != null
                                && group.getLeader().getId().equals(user.getId());

                boolean isMember = group.getMembers().stream()
                                .anyMatch(member -> member.getUser().getId().equals(user.getId()));

                if (!isLeader && !isMember) {
                        throw new IllegalArgumentException(
                                        "Você não possui acesso a este grupo.");
                }

                return new GroupResponseDTO(group);
        }

        private String generateInviteCode() {

                String code;

                do {
                        StringBuilder sb = new StringBuilder(6);

                        for (int i = 0; i < 6; i++) {
                                sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
                        }

                        code = sb.toString();

                } while (groupRepository.existsByInviteCode(code));

                return code;
        }
}
