package ajudavcapi.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.group.CreateGroupDTO;
import ajudavcapi.domain.dto.group.GroupResponseDTO;
import ajudavcapi.domain.dto.group.JoinGroupDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.PatientEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.UserRepository;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Cria um novo grupo, associa o paciente e atribui o líder.
     * Retorna o GroupResponseDTO esperado pelo Controller.
     */
    @Transactional
    public GroupResponseDTO createGroup(CreateGroupDTO dto, UserEntity leader) {
        
        // 1. Cria e preenche os dados do Paciente
        PatientEntity patient = new PatientEntity();
        patient.setName(dto.patientName());
        
        // 2. Instancia o Grupo
        GroupEntity group = new GroupEntity();
        group.setName(dto.name());
        group.setLeader(leader);
        group.setInviteCode(generateInviteCode());

        // 3. Associação obrigatória (Cascade salva o paciente)
        group.setPatient(patient);

        // 4. Salva o Grupo
        GroupEntity savedGroup = groupRepository.save(group);

        // 5. Atualiza o grupo no usuário líder
        leader.setGroup(savedGroup);
        userRepository.save(leader);

        // Retorna mapeado para DTO
        return new GroupResponseDTO(savedGroup);
    }

    /**
     * Associa um usuário a um grupo existente utilizando o código de convite.
     */
    @Transactional
    public GroupResponseDTO joinGroup(JoinGroupDTO dto, UserEntity user) {
        GroupEntity group = groupRepository.findByInviteCode(dto.inviteCode())
                .orElseThrow(() -> new RuntimeException("Código de convite inválido ou grupo não encontrado."));

        user.setGroup(group);
        userRepository.save(user);

        return new GroupResponseDTO(group);
    }

    /**
     * Retorna as informações do grupo ao qual o usuário logado pertence.
     */
    @Transactional(readOnly = true)
    public GroupResponseDTO getMyGroup(UserEntity user) {
        if (user.getGroup() == null) {
            throw new RuntimeException("O usuário não pertence a nenhum grupo.");
        }
        return new GroupResponseDTO(user.getGroup());
    }

    /**
     * Busca um grupo pelo ID e valida se o usuário tem permissão de acesso.
     */
    @Transactional(readOnly = true)
    public GroupResponseDTO getGroupById(Long id, UserEntity user) {
        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));

        // Validação: Garante que o usuário pertence ao grupo que está tentando acessar
        if (user.getGroup() == null || !user.getGroup().getId().equals(group.getId())) {
            throw new RuntimeException("Acesso negado a este grupo.");
        }

        return new GroupResponseDTO(group);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}