package com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto;

import java.time.Instant;

public record LoginResponse(
        String token,
        Instant expiresAt
) {}
