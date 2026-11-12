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
import ajudavcapi.domain.entity.StrokeEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.entity.DiseaseEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.exception.GroupNotFoundException;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.UserRepository;

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
                        throw new IllegalArgumentException(
                                        "O usuário já está associado a um grupo.");
                }

                GroupEntity group = new GroupEntity();

                group.setName(dto.name());
                group.setLeader(managedLeader);
                managedLeader.setGroup(group);
                group.setInviteCode(generateInviteCode());

                if (dto.patient() != null) {

                        PatientEntity patient = new PatientEntity();

                        patient.setName(dto.patient().name().trim());
                        patient.setBirthDate(dto.patient().birthDate());
                        patient.setImportantDescription(
                                        dto.patient().importantDescription());

                        if (dto.patient().strokes() != null) {

                                for (ajudavcapi.domain.dto.stroke.CreateStrokeDTO strokeDTO : dto.patient().strokes()) {

                                        StrokeEntity stroke = new StrokeEntity();

                                        stroke.setStrokeType(
                                                        strokeDTO.strokeType());

                                        stroke.setStrokeDate(
                                                        strokeDTO.strokeDate());

                                        patient.addStroke(stroke);
                                }
                        }

                        if (dto.patient().diseases() != null) {
                                for (Object diseaseObj : dto.patient().diseases()) {
                                        DiseaseEntity disease = new DiseaseEntity();
                                        String diseaseStr = String.valueOf(diseaseObj).trim().toUpperCase();
                                        disease.setType(ajudavcapi.domain.enums.DiseaseType.valueOf(diseaseStr));

                                        patient.addDisease(disease);
                                }
                        }
                        patient.setGroup(group);
                        group.setPatient(patient);
                }

                GroupMemberEntity leaderMember = new GroupMemberEntity();

                leaderMember.setUser(managedLeader);
                leaderMember.setRole(GroupRole.LEADER);

                group.addMember(leaderMember);

                GroupEntity savedGroup = groupRepository.save(group);

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

                GroupEntity updatedGroup = groupRepository.save(group);

                return new GroupResponseDTO(updatedGroup);
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
                managedUser.setGroup(group);

                GroupEntity savedGroup = groupRepository.save(group);

                return new GroupResponseDTO(savedGroup);
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

                GroupEntity group = groupRepository.findById(id)
                                .orElseThrow(() -> new GroupNotFoundException(
                                                "Grupo não encontrado para o ID especificado."));

                if (user == null || user.getId() == null) {
                        throw new IllegalArgumentException(
                                        "Usuário autenticado não identificado.");
                }

                boolean isMemberOrLeader = group.getLeader() != null
                                && group.getLeader().getId().equals(user.getId())
                                || group.getMembers().stream()
                                                .anyMatch(member -> member.getUser().getId().equals(user.getId()));

                if (!isMemberOrLeader) {
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

                                sb.append(
                                                CHARACTERS.charAt(
                                                                RANDOM.nextInt(
                                                                                CHARACTERS.length())));
                        }

                        code = sb.toString();

                } while (groupRepository.existsByInviteCode(code));

                return code;
        }
}