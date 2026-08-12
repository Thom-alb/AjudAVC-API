package ajudavcapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import ajudavcapi.domain.dto.patient.PatientResponseDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.PatientEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final GroupRepository groupRepository;

    // Injeção via construtor (melhor prática que @Autowired em atributos)
    public PatientService(PatientRepository patientRepository, GroupRepository groupRepository) {
        this.patientRepository = patientRepository;
        this.groupRepository = groupRepository;
    }

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

        // 2. Instancia e preenche o Paciente
        PatientEntity patient = new PatientEntity();
        patient.setName(dto.name());
        patient.setStrokeType(dto.strokeType());
        patient.setBirthDate(dto.birthDate());
        patient.setStrokeDate(dto.strokeDate());
        patient.setImportantDescription(dto.importantDescription());
        patient.setGroup(group);

        PatientEntity savedPatient = patientRepository.save(patient);

        // 3. Atualiza o relacionamento no grupo (O contexto de persistência JPA sincroniza automaticamente)
        group.setPatient(savedPatient);

        return mapToResponseDTO(savedPatient, group.getId());
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByGroup(UserEntity userLogado) {
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

        return mapToResponseDTO(patient, group.getId());
    }

    // Método auxiliar para evitar duplicação do mapeamento do DTO
    private PatientResponseDTO mapToResponseDTO(PatientEntity patient, Long groupId) {
        return new PatientResponseDTO(
            patient.getId(),
            patient.getName(),
            patient.getStrokeType(),
            patient.getBirthDate(),
            patient.getStrokeDate(),
            patient.getImportantDescription(),
            groupId
        );
    }
}