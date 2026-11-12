package ajudavcapi.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import ajudavcapi.domain.dto.patient.PatientResponseDTO;
import ajudavcapi.domain.dto.patient.UpdatePatientDTO;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.PatientEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.mapper.PatientMapper;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.PatientRepository;
import ajudavcapi.exception.ConflictException;
import ajudavcapi.exception.GroupNotFoundException;
import ajudavcapi.exception.ResourceNotFoundException;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final GroupRepository groupRepository;

    public PatientService(
            PatientRepository patientRepository,
            GroupRepository groupRepository) {

        this.patientRepository = patientRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public PatientResponseDTO createPatient(
            CreatePatientDTO dto,
            UserEntity userLogado) {

        GroupEntity group = findGroupByUser(userLogado);

        if (group.getPatient() != null) {
            throw new ConflictException(
                    "Este grupo já possui um paciente cadastrado.");
        }

        // persiste paciente + AVCs + doenças (cascade) e obtém o id
        PatientEntity patient = patientRepository.save(PatientMapper.toEntity(dto));

        // o GroupEntity é o dono da FK: grava care_groups.patient_id
        group.setPatient(patient);
        groupRepository.flush();

        return PatientMapper.toResponse(patient, group.getId());
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByGroup(UserEntity userLogado) {

        GroupEntity group = findGroupByUser(userLogado);

        PatientEntity patient = group.getPatient();

        if (patient == null) {
            throw new ResourceNotFoundException(
                    "Nenhum paciente cadastrado para este grupo.");
        }

        return PatientMapper.toResponse(patient, group.getId());
    }

    @Transactional
    public PatientResponseDTO updatePatient(
            Long id,
            UpdatePatientDTO dto,
            UserEntity userLogado) {

        GroupEntity group = findGroupByUser(userLogado);

        PatientEntity patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente não encontrado com o ID fornecido."));

        if (group.getPatient() == null || !group.getPatient().getId().equals(patient.getId())) {
            throw new AccessDeniedException(
                    "Você não tem permissão para alterar este paciente.");
        }

        PatientMapper.applyUpdate(patient, dto);

        patientRepository.flush(); // INSERT/UPDATE/DELETE de AVCs e doenças

        return PatientMapper.toResponse(patient, group.getId());
    }

    /**
     * SEMPRE recarrega o grupo dentro da transação atual.
     *
     * Antes: usava userLogado.getGroup(). O userLogado vem do SecurityFilter, que
     * o carrega fora de transação (open-in-view=false); seu "group" é um proxy
     * LAZY desanexado, e qualquer acesso (ex.: group.getPatient()) lança
     * LazyInitializationException -> HTTP 500.
     */
    private GroupEntity findGroupByUser(UserEntity userLogado) {

        if (userLogado == null || userLogado.getId() == null) {
            throw new AccessDeniedException("Usuário autenticado não identificado.");
        }

        return groupRepository
                .findByLeaderIdOrMembersUserId(userLogado.getId(), userLogado.getId())
                .orElseThrow(() -> new GroupNotFoundException(
                        "Usuário não está vinculado a um grupo."));
    }
}
