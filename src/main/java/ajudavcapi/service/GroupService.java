package ajudavcapi.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.group.CreateGroupDTO;
import ajudavcapi.domain.dto.group.GroupResponseDTO;
import ajudavcapi.domain.dto.group.JoinGroupDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.GroupMemberEntity;
import ajudavcapi.domain.entity.PatientEntity;
import ajudavcapi.domain.entity.StrokeEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.enums.GroupRole;
import ajudavcapi.domain.repository.GroupRepository;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final SecureRandom RANDOM =
            new SecureRandom();

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional
    public GroupResponseDTO createGroup(
            CreateGroupDTO dto,
            UserEntity leader) {

        GroupEntity group = new GroupEntity();

        group.setName(dto.name());
        group.setLeader(leader);
        group.setInviteCode(generateInviteCode());

        if (dto.patient() != null) {

            PatientEntity patient = new PatientEntity();

            patient.setName(dto.patient().name().trim());
            patient.setBirthDate(dto.patient().birthDate());
            patient.setImportantDescription(
                    dto.patient().importantDescription()
            );

            if (dto.patient().strokes() != null) {

                for (var strokeDTO : dto.patient().strokes()) {

                    StrokeEntity stroke = new StrokeEntity();

                    stroke.setStrokeType(
                            strokeDTO.strokeType()
                    );

                    stroke.setStrokeDate(
                            strokeDTO.strokeDate()
                    );

                    stroke.setPatient(patient);

                    patient.getStrokes().add(stroke);
                }
            }

            patient.setGroup(group);
            group.setPatient(patient);
        }

        GroupMemberEntity leaderMember =
                new GroupMemberEntity();

        leaderMember.setUser(leader);
        leaderMember.setRole(GroupRole.LEADER);

        group.addMember(leaderMember);

        GroupEntity savedGroup =
                groupRepository.save(group);

        return new GroupResponseDTO(savedGroup);
    }

    @Transactional
    public GroupResponseDTO joinGroup(
            JoinGroupDTO dto,
            UserEntity user) {

        GroupEntity group =
                groupRepository
                        .findByInviteCode(dto.inviteCode())
                        .orElseThrow(() ->
                                new RuntimeException(
                                    "Código de convite inválido ou grupo não encontrado."
                                )
                        );

        boolean alreadyMember =
                group.getMembers()
                        .stream()
                        .anyMatch(m ->
                                m.getUser()
                                 .getId()
                                 .equals(user.getId())
                        );

        if (alreadyMember) {
            throw new RuntimeException(
                    "O usuário já faz parte deste grupo."
            );
        }

        GroupMemberEntity newMember =
                new GroupMemberEntity();

        newMember.setUser(user);
        newMember.setRole(GroupRole.MEMBER);

        group.addMember(newMember);

        groupRepository.save(group);

        return new GroupResponseDTO(group);
    }

    @Transactional(readOnly = true)
    public GroupResponseDTO getMyGroup(
            UserEntity user) {

        GroupEntity group =
                groupRepository
                        .findByLeaderIdOrMembersUserId(
                                user.getId(),
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                    "O usuário não pertence a nenhum grupo de cuidado."
                                )
                        );

        return new GroupResponseDTO(group);
    }

    @Transactional(readOnly = true)
    public GroupResponseDTO getGroupById(
            Long id,
            UserEntity user) {

        GroupEntity group =
                groupRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                    "Grupo não encontrado para o ID especificado."
                                )
                        );

        boolean isMemberOrLeader =
                group.getLeader()
                        .getId()
                        .equals(user.getId())
                ||
                group.getMembers()
                        .stream()
                        .anyMatch(m ->
                                m.getUser()
                                 .getId()
                                 .equals(user.getId())
                        );

        if (!isMemberOrLeader) {
            throw new RuntimeException(
                    "Acesso negado: você não pertence a este grupo."
            );
        }

        return new GroupResponseDTO(group);
    }

    private String generateInviteCode() {

        String code;

        do {

            StringBuilder sb =
                    new StringBuilder(6);

            for (int i = 0; i < 6; i++) {

                sb.append(
                        CHARACTERS.charAt(
                                RANDOM.nextInt(
                                        CHARACTERS.length()
                                )
                        )
                );
            }

            code = sb.toString();

        } while (
                groupRepository.existsByInviteCode(code)
        );

        return code;
    }
}