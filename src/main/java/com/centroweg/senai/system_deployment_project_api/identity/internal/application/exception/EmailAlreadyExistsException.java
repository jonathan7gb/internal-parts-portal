package com.centroweg.senai.system_deployment_project_api.identity.internal.application.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
