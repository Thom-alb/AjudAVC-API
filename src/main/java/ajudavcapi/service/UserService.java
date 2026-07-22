package ajudavcapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ajudavcapi.domain.dto.user.UserRequestDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> listarUsuarios() {
        List<UserEntity> users = userRepository.findAll();
        return users;
    }

    public UserEntity adicionarUsuario(UserRequestDTO user) {
        if (userRepository.existsByEmail(user.email())) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }


        UserEntity u = new UserEntity();
        u.setEmail(user.email());
        u.setPassword(user.password());
        u.setName(user.name());

        return userRepository.save(u);
    }
}
