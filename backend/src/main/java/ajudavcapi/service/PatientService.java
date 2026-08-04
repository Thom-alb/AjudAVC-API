package ajudavcapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.PatientEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.PatientRepository;
import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import ajudavcapi.domain.dto.patient.PatientResponseDTO;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public PatientResponseDTO createPatient(CreatePatientDTO dto, UserEntity userLogado) {
        // 1. Busca o grupo do Líder
        GroupEntity group = groupRepository.findByLeader(userLogado)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui um grupo registrado."));

        if (group.getPatient() != null) {
            throw new IllegalArgumentException("Este grupo já possui um paciente cadastrado.");
        }

        // 2. Criação da Entidade Paciente com os getters corretos do record
        PatientEntity patient = new PatientEntity();
        patient.setName(dto.name());
        patient.setStrokeType(dto.strokeType()); // Corrigido!
        patient.setBirthDate(dto.birthDate());
        patient.setStrokeDate(dto.strokeDate());
        patient.setGroup(group);

        PatientEntity savedPatient = patientRepository.save(patient);

        // Vincula o paciente salvo no grupo
        group.setPatient(savedPatient);
        groupRepository.save(group);

        return new PatientResponseDTO(
            savedPatient.getId(),
            savedPatient.getName(),
            savedPatient.getBirthDate(),
            savedPatient.getStrokeDate(),
            group.getId()
        );
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByGroup(UserEntity userLogado) {
        // Resolve a chamada do getGroup() na UserEntity
        GroupEntity group = userLogado.getGroup();
        
        if (group == null) {
            group = groupRepository.findByLeader(userLogado)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não está vinculado a um grupo."));
        }

        PatientEntity patient = group.getPatient();
        if (patient == null) {
            throw new IllegalArgumentException("Nenhum paciente cadastrado para este grupo.");
        }

        return new PatientResponseDTO(
            patient.getId(),
            patient.getName(),
            patient.getBirthDate(),
            patient.getStrokeDate(),
            group.getId()
        );
    }
}