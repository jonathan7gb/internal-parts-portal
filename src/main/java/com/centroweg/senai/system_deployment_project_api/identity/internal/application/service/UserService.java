package com.centroweg.senai.system_deployment_project_api.identity.internal.application.service;

import com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto.*;
import com.centroweg.senai.system_deployment_project_api.identity.internal.application.exception.*;
import com.centroweg.senai.system_deployment_project_api.identity.internal.domain.model.User;
import com.centroweg.senai.system_deployment_project_api.identity.internal.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de gerenciamento de usuários.
 *
 * <p>Regras de negócio críticas:
 * <ul>
 *   <li>Senha armazenada exclusivamente como hash bcrypt.</li>
 *   <li>Admin não pode se auto-desativar.</li>
 *   <li>Email deve ser único no sistema.</li>
 *   <li>Troca de senha exige a senha atual correta.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> listAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.email());
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .active(true)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = findOrThrow(id);

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.email());
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());

        return UserResponse.from(userRepository.save(user));
    }

    public void deactivate(UUID id, UUID authenticatedUserId) {
        if (id.equals(authenticatedUserId)) {
            throw new SelfDeactivationException("Admin cannot deactivate their own account");
        }

        User user = findOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        return UserResponse.from(findOrThrow(userId));
    }

    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = findOrThrow(userId);

        user.setName(request.name());

        boolean wantsToChangePassword = request.newPassword() != null
                && !request.newPassword().isBlank();

        if (wantsToChangePassword) {
            if (request.currentPassword() == null
                    || !passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }

        return UserResponse.from(userRepository.save(user));
    }

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }
}
