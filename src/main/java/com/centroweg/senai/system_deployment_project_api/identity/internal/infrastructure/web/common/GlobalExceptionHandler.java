package com.centroweg.senai.system_deployment_project_api.identity.internal.infrastructure.web.common;

import com.centroweg.senai.system_deployment_project_api.identity.internal.application.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Handler global de exceções — aplica a todos os controllers da aplicação.
 *
 * <p>Garante respostas de erro consistentes em JSON para qualquer módulo.
 * Cada módulo pode adicionar seu próprio {@code @RestControllerAdvice} para
 * exceções específicas sem conflito.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // 400 — Bad Request
    // -------------------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return new ErrorResponse(400, "Bad Request", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleUnreadable(HttpMessageNotReadableException ex) {
        return new ErrorResponse(400, "Bad Request", "Malformed or missing request body");
    }

    @ExceptionHandler({SelfDeactivationException.class, InvalidPasswordException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleBadRequest(RuntimeException ex) {
        return new ErrorResponse(400, "Bad Request", ex.getMessage());
    }

    // -------------------------------------------------------------------------
    // 401 — Unauthorized
    // -------------------------------------------------------------------------

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleUnauthorized(RuntimeException ex) {
        return new ErrorResponse(401, "Unauthorized", ex.getMessage());
    }

    // -------------------------------------------------------------------------
    // 404 — Not Found
    // -------------------------------------------------------------------------

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleNotFound(UserNotFoundException ex) {
        return new ErrorResponse(404, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleNoResource(NoResourceFoundException ex) {
        return new ErrorResponse(404, "Not Found", "The requested resource does not exist");
    }

    // -------------------------------------------------------------------------
    // 409 — Conflict
    // -------------------------------------------------------------------------

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleConflict(EmailAlreadyExistsException ex) {
        return new ErrorResponse(409, "Conflict", ex.getMessage());
    }

    // -------------------------------------------------------------------------
    // 500 — Fallback
    // -------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleGeneric(Exception ex) {
        return new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred");
    }

    // -------------------------------------------------------------------------
    // Schema de resposta
    // -------------------------------------------------------------------------

    record ErrorResponse(int status, String error, String message, String timestamp) {
        ErrorResponse(int status, String error, String message) {
            this(status, error, message, Instant.now().toString());
        }
    }
}
