package com.centroweg.senai.system_deployment_project_api.identity.internal.application.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
