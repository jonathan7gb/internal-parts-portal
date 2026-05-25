package com.centroweg.senai.system_deployment_project_api.orders.internal.infrastructure.web;

import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception.InvalidOrderStateException;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception.OrderNotFoundException;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception.StockUnavailableException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = OrderController.class)
class OrderExceptionHandler {

    record ErrorResponse(String message, Instant timestamp) {
        static ErrorResponse of(String message) {
            return new ErrorResponse(message, Instant.now());
        }
    }

    record StockErrorResponse(String message, List<StockUnavailableException.UnavailableItem> unavailableItems) {}

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleNotFound(OrderNotFoundException ex) {
        return ErrorResponse.of(ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleInvalidState(InvalidOrderStateException ex) {
        return ErrorResponse.of(ex.getMessage());
    }

    @ExceptionHandler(StockUnavailableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    StockErrorResponse handleStockUnavailable(StockUnavailableException ex) {
        return new StockErrorResponse("Stock unavailable for one or more items", ex.getUnavailableItems());
    }

    @ExceptionHandler(ForbiddenOrderAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResponse handleForbidden(ForbiddenOrderAccessException ex) {
        return ErrorResponse.of(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Invalid request");
        return ErrorResponse.of(message);
    }
}
