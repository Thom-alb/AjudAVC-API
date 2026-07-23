package ajudavcapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ajudavcapi.domain.dto.user.UserRequestDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> listarUsuarios() {
        return userRepository.findAll();
    }

    public UserEntity buscarPorId(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para o ID: " + id));
    }

    public UserEntity adicionarUsuario(UserRequestDTO user) {
        if (userRepository.existsByEmail(user.email())) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        UserEntity u = new UserEntity();
        u.setName(user.name());
        u.setEmail(user.email());
        u.setPassword(user.password());

        return userRepository.save(u);
    }

    public UserEntity atualizarUsuario(Long id, UserRequestDTO dto) {
        UserEntity user = buscarPorId(id);

        if (!user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Este e-mail já está em uso.");
        }

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        return userRepository.save(user);
    }

    public void deletarUsuario(Long id) {
        UserEntity user = buscarPorId(id);
        userRepository.delete(user);
    }
}