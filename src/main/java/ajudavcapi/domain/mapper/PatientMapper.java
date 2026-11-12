package ajudavcapi.domain.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ajudavcapi.domain.dto.disease.DiseaseResponseDTO;
import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import ajudavcapi.domain.dto.patient.PatientResponseDTO;
import ajudavcapi.domain.dto.patient.UpdatePatientDTO;
import ajudavcapi.domain.dto.stroke.CreateStrokeDTO;
import ajudavcapi.domain.dto.stroke.StrokeResponseDTO;
import ajudavcapi.domain.dto.stroke.UpdateStrokeDTO;
import ajudavcapi.domain.entity.DiseaseEntity;
import ajudavcapi.domain.entity.PatientEntity;
import ajudavcapi.domain.entity.StrokeEntity;
import ajudavcapi.domain.enums.DiseaseType;
import ajudavcapi.domain.enums.StrokeType;

/**
 * Única fonte de verdade para montar/atualizar/serializar PatientEntity.
 * Usada por GroupService (POST /groups com paciente), PatientService
 * (POST/PUT /patients) e GroupResponseDTO, para que os três fluxos se comportem
 * exatamente igual e sejam null-safe.
 */
public final class PatientMapper {

    private PatientMapper() {
    }

    /* ----------------------------- DTO -> Entity ----------------------------- */

    public static PatientEntity toEntity(CreatePatientDTO dto) {

        PatientEntity patient = new PatientEntity();

        patient.setName(dto.name().trim());
        patient.setBirthDate(dto.birthDate());
        patient.setImportantDescription(normalizeText(dto.importantDescription()));

        if (dto.strokes() != null) {
            for (CreateStrokeDTO strokeDTO : dto.strokes()) {
                if (strokeDTO == null) {
                    continue;
                }

                patient.addStroke(newStroke(strokeDTO.strokeType(), strokeDTO.strokeDate()));
            }
        }

        for (DiseaseType type : distinctDiseases(dto.diseases())) {
            patient.addDisease(newDisease(type));
        }

        return patient;
    }

    /**
     * Aplica o PUT sobre uma entidade JÁ GERENCIADA.
     * - AVCs: com id => atualiza; sem id => cria; ausentes do payload => removidos.
     * - Doenças: o conjunto do payload substitui o atual (sem duplicar).
     */
    public static void applyUpdate(PatientEntity patient, UpdatePatientDTO dto) {

        patient.setName(dto.name().trim());
        patient.setBirthDate(dto.birthDate());
        patient.setImportantDescription(normalizeText(dto.importantDescription()));

        syncStrokes(patient, dto.strokes());
        syncDiseases(patient, dto.diseases());
    }

    private static void syncStrokes(PatientEntity patient, List<UpdateStrokeDTO> incoming) {

        if (incoming == null) {
            return; // null = não mexer nos AVCs
        }

        Map<Long, StrokeEntity> existingById = new HashMap<>();

        for (StrokeEntity stroke : patient.getStrokes()) {
            if (stroke.getId() != null) {
                existingById.put(stroke.getId(), stroke);
            }
        }

        Set<StrokeEntity> keep = Collections.newSetFromMap(new IdentityHashMap<>());
        List<StrokeEntity> toAdd = new ArrayList<>();

        for (UpdateStrokeDTO strokeDTO : incoming) {
            if (strokeDTO == null) {
                continue;
            }

            if (strokeDTO.id() != null) {
                StrokeEntity existing = existingById.get(strokeDTO.id());

                if (existing == null) {
                    throw new IllegalArgumentException(
                            "O AVC de id " + strokeDTO.id() + " não pertence a este paciente.");
                }

                existing.setStrokeType(strokeDTO.strokeType());
                existing.setStrokeDate(strokeDTO.strokeDate());
                keep.add(existing);
            } else {
                toAdd.add(newStroke(strokeDTO.strokeType(), strokeDTO.strokeDate()));
            }
        }

        // orphanRemoval => DELETE das linhas retiradas da coleção
        patient.getStrokes().removeIf(stroke -> !keep.contains(stroke));

        toAdd.forEach(patient::addStroke);
    }

    private static void syncDiseases(PatientEntity patient, List<DiseaseType> incoming) {

        if (incoming == null) {
            return; // null = não mexer nas doenças
        }

        Set<DiseaseType> desired = distinctDiseases(incoming);
        Set<DiseaseType> present = new HashSet<>();

        // remove as que saíram do payload e eventuais duplicatas já gravadas
        patient.getDiseases().removeIf(disease -> !desired.contains(disease.getType())
                || !present.add(disease.getType()));

        for (DiseaseType type : desired) {
            if (!present.contains(type)) {
                patient.addDisease(newDisease(type));
            }
        }
    }

    /* ----------------------------- Entity -> DTO ----------------------------- */

    /** Retorna null quando não há paciente. Deve ser chamado dentro da transação (coleções LAZY). */
    public static PatientResponseDTO toResponse(PatientEntity patient, Long groupId) {

        if (patient == null) {
            return null;
        }

        List<StrokeResponseDTO> strokes = patient.getStrokes() == null
                ? List.of()
                : patient.getStrokes().stream()
                        .map(s -> new StrokeResponseDTO(s.getId(), s.getStrokeType(), s.getStrokeDate()))
                        .toList();

        List<DiseaseResponseDTO> diseases = patient.getDiseases() == null
                ? List.of()
                : patient.getDiseases().stream()
                        .map(d -> new DiseaseResponseDTO(d.getId(), d.getType()))
                        .toList();

        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getBirthDate(),
                patient.getImportantDescription(),
                strokes,
                diseases,
                groupId);
    }

    /* -------------------------------- Helpers -------------------------------- */

    private static StrokeEntity newStroke(StrokeType type, java.time.LocalDate date) {
        StrokeEntity stroke = new StrokeEntity();
        stroke.setStrokeType(type);
        stroke.setStrokeDate(date);
        return stroke;
    }

    private static DiseaseEntity newDisease(DiseaseType type) {
        DiseaseEntity disease = new DiseaseEntity();
        disease.setType(type);
        return disease;
    }

    private static Set<DiseaseType> distinctDiseases(Collection<DiseaseType> types) {
        Set<DiseaseType> result = new LinkedHashSet<>();

        if (types != null) {
            for (DiseaseType type : types) {
                if (type != null) {
                    result.add(type);
                }
            }
        }

        return result;
    }

    private static String normalizeText(String text) {
        if (text == null) {
            return null;
        }

        String trimmed = text.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }
}
