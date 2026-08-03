package ajudavcapi.domain.dto.group;

public record GroupResponseDTO(
    Long id,
    String name,
    String inviteCode,
    Long leaderId
) {}