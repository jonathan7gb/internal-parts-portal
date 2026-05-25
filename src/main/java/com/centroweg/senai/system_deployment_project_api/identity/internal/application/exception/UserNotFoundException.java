package com.centroweg.senai.system_deployment_project_api.identity.internal.application.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
