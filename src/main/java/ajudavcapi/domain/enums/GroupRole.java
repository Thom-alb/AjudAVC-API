package ajudavcapi.domain.enums;

public enum GroupRole {
    LEADER("LEADER"), // Dono do grupo, gerencia permissões e atua como membro
    MEMBER("MEMBER"); // Membro do grupo, participa das atividades e tarefas dentro do app

    private String role;

    GroupRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}   