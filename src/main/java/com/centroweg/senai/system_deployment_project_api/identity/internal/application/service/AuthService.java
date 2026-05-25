package com.centroweg.senai.system_deployment_project_api.identity.internal.application.service;

import com.centroweg.senai.system_deployment_project_api.identity.UserDetailsPort;
import com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto.LoginRequest;
import com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto.LoginResponse;
import com.centroweg.senai.system_deployment_project_api.identity.internal.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Caso de uso: autenticação de usuários.
 *
 * <p>Valida credenciais manualmente (sem delegar ao {@code AuthenticationManager}) para ter
 * controle total sobre as mensagens de erro retornadas ao cliente.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsPort userDetailsPort;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Autentica o usuário e retorna um JWT.
     *
     * @throws DisabledException     se {@code active = false}
     * @throws BadCredentialsException se a senha estiver incorreta
     */
    public LoginResponse login(LoginRequest request) {
        User user = (User) userDetailsPort.loadUserByUsername(request.email());

        if (!user.isEnabled()) {
            throw new DisabledException("User account is inactive");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generate(user.getId(), user.getRole());
        Instant expiresAt = jwtService.extractExpiration(token);

        return new LoginResponse(token, expiresAt);
    }
}
