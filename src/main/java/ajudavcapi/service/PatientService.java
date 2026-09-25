package ajudavcapi.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ajudavcapi.domain.dto.disease.DiseaseResponseDTO;
import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import ajudavcapi.domain.dto.patient.PatientResponseDTO;
import ajudavcapi.domain.dto.patient.UpdatePatientDTO;
import ajudavcapi.domain.dto.stroke.StrokeResponseDTO;
import ajudavcapi.domain.entity.DiseaseEntity;
import ajudavcapi.domain.entity.GroupEntity;
import ajudavcapi.domain.entity.PatientEntity;
import ajudavcapi.domain.entity.StrokeEntity;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.GroupRepository;
import ajudavcapi.domain.repository.PatientRepository;

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
            throw new IllegalArgumentException(
                "Este grupo já possui um paciente cadastrado."
            );
        }

        PatientEntity patient = new PatientEntity();
        patient.setName(dto.name());
        patient.setBirthDate(dto.birthDate());
        patient.setImportantDescription(dto.importantDescription());
        patient.setGroup(group);

        if (dto.strokes() != null) {
            for (var strokeDTO : dto.strokes()) {
                StrokeEntity stroke = new StrokeEntity();
                stroke.setStrokeType(strokeDTO.strokeType());
                stroke.setStrokeDate(strokeDTO.strokeDate());
                stroke.setPatient(patient);

                patient.getStrokes().add(stroke);
            }
        }

        if (dto.diseases() != null) {
            for (var diseaseType : dto.diseases()) {
                DiseaseEntity disease = new DiseaseEntity();
                disease.setType(diseaseType);
                disease.setPatient(patient);

                patient.getDiseases().add(disease);
            }
        }

        PatientEntity savedPatient = patientRepository.saveAndFlush(patient);

        group.setPatient(savedPatient);
        groupRepository.save(group);

        return mapToResponseDTO(
            savedPatient,
            group.getId()
        );
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByGroup(
            UserEntity userLogado) {

        GroupEntity group = findGroupByUser(userLogado);

        PatientEntity patient = group.getPatient();

        if (patient == null) {
            throw new IllegalArgumentException(
                "Nenhum paciente cadastrado para este grupo."
            );
        }

        return mapToResponseDTO(
            patient,
            group.getId()
        );
    }

    @Transactional
    public PatientResponseDTO updatePatient(
            Long id,
            UpdatePatientDTO dto,
            UserEntity userLogado) {

        GroupEntity group = findGroupByUser(userLogado);

        PatientEntity patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Paciente não encontrado com o ID fornecido."
                ));

        if (group.getPatient() == null || !group.getPatient().getId().equals(patient.getId())) {
            throw new IllegalArgumentException(
                "Você não tem permissão para alterar este paciente."
            );
        }

        patient.setName(dto.name());
        patient.setBirthDate(dto.birthDate());
        patient.setImportantDescription(dto.importantDescription());

        if (dto.strokes() != null) {
            patient.getStrokes().clear();
            for (var strokeDTO : dto.strokes()) {
                StrokeEntity stroke = new StrokeEntity();
                stroke.setStrokeType(strokeDTO.strokeType());
                stroke.setStrokeDate(strokeDTO.strokeDate());
                stroke.setPatient(patient);

                patient.getStrokes().add(stroke);
            }
        }

        if (dto.diseases() != null) {
            patient.getDiseases().clear();
            for (var diseaseType : dto.diseases()) {
                DiseaseEntity disease = new DiseaseEntity();
                disease.setType(diseaseType);
                disease.setPatient(patient);

                patient.getDiseases().add(disease);
            }
        }

        PatientEntity updatedPatient = patientRepository.saveAndFlush(patient);

        return mapToResponseDTO(
            updatedPatient,
            group.getId()
        );
    }

    private GroupEntity findGroupByUser(UserEntity userLogado) {
        if (userLogado.getGroup() != null) {
            return userLogado.getGroup();
        }

        return groupRepository.findByLeader(userLogado)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Usuário não está vinculado a um grupo."
                ));
    }

    private PatientResponseDTO mapToResponseDTO(
            PatientEntity patient,
            Long groupId) {

        List<StrokeResponseDTO> strokes = patient.getStrokes() == null 
                ? Collections.emptyList() 
                : patient.getStrokes()
                    .stream()
                    .map(stroke -> new StrokeResponseDTO(
                        stroke.getId(),
                        stroke.getStrokeType(),
                        stroke.getStrokeDate()
                    ))
                    .toList();

        List<DiseaseResponseDTO> diseases = patient.getDiseases() == null 
                ? Collections.emptyList() 
                : patient.getDiseases()
                    .stream()
                    .map(disease -> new DiseaseResponseDTO(
                        disease.getId(),
                        disease.getType()
                    ))
                    .toList();

        return new PatientResponseDTO(
            patient.getId(),
            patient.getName(),
            patient.getBirthDate(),
            patient.getImportantDescription(),
            strokes,
            diseases,
            groupId
        );
    }
}