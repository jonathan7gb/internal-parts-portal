package com.centroweg.senai.system_deployment_project_api.identity.internal.application.exception;

public class SelfDeactivationException extends RuntimeException {
    public SelfDeactivationException(String message) {
        super(message);
    }
}
