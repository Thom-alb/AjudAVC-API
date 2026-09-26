package ajudavcapi.domain.dto.patient;

import java.time.LocalDate;
import java.util.List;

import ajudavcapi.domain.dto.disease.DiseaseResponseDTO;
import ajudavcapi.domain.dto.stroke.StrokeResponseDTO;

public record PatientResponseDTO(

    Long id,

    String name,

    LocalDate birthDate,

    String importantDescription,

    List<StrokeResponseDTO> strokes,

    List<DiseaseResponseDTO> diseases,

    Long groupId

) {}