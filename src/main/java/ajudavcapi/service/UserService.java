
package ajudavcapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ajudavcapi.domain.dto.user.UserRequestDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public List<UserEntity> listarUsuarios() {
        return userRepository.findAll();
    }

    public UserEntity buscarPorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado para o ID: " + id));
    }

    public UserEntity adicionarUsuario(UserRequestDTO user) {
        if (userRepository.existsByEmail(user.email())) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }

        UserEntity u = new UserEntity();
        u.setName(user.name());
        u.setEmail(user.email());

        if (user.password() == null || user.password().isBlank()) {
            u.setPassword(null);
            u.setProvider("GOOGLE");
        } else {
            u.setPassword(passwordEncoder.encode(user.password()));
            u.setProvider("LOCAL");
        }

        return userRepository.save(u);
    }

    public UserEntity atualizarUsuario(Long id, UserRequestDTO dto) {
        UserEntity user = buscarPorId(id);

        if (!user.getEmail().equals(dto.email())
                && userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException(
                    "Este e-mail já está em uso.");
        }

        user.setName(dto.name());
        user.setEmail(dto.email());

        // Não alteramos senha nem provedor nesta atualização.

        return userRepository.save(user);
    }


    public UserEntity salvarUsuario(UserEntity user) {
        return userRepository.save(user);
    }

    public Optional<UserEntity> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public void deletarUsuario(Long id) {
        UserEntity user = buscarPorId(id);
        userRepository.delete(user);
    }
}
