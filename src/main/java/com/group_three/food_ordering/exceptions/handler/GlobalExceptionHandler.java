package com.group_three.food_ordering.exceptions.handler;

import com.group_three.food_ordering.exceptions.*;
import com.group_three.food_ordering.exceptions.responses.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Error de entidad no encontrada en la base de datos
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        log.warn("[{}] {}", e.getEntityName(), e.getMessage());
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, request);
    }

    // Error de intento de modificación de un pago ya completado o cancelado
    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentStatus(InvalidPaymentStatusException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.CONFLICT, request);
    }

    // Error de intento de modificación de una Orden ya en progreso
    @ExceptionHandler(OrderInProgressException.class)
    public ResponseEntity<ErrorResponse> handleOrderInProgress(OrderInProgressException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.CONFLICT, request);
    }

    // Error de acceso denegado por lógica de negocio
    @ExceptionHandler(LogicalAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleLogicalAccessDenied(LogicalAccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, request);
    }

    // Error de intento de registrar un usuario con un email ya existente
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyUsed(EmailAlreadyUsedException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    // Error de solicitar más productos que los disponibles
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.CONFLICT, request);
    }

    // Error de contexto indeterminado cuando no se puede obtener el Food Venue
    @ExceptionHandler(MissingTenantContextException.class)
    public ResponseEntity<ErrorResponse> handleIndeterminateTenantContext(MissingTenantContextException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "EXPIRED_TOKEN");
        error.put("message", "Access token has expired. Please use the refresh token.");
        error.put("timestamp", Instant.now().toString());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Errores de validación de DTO (@Valid fallidos)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    // JSON mal formado o tipo de datos incompatible en el request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    // IllegalArgument o IllegalState: cuando el flujo de la lógica falla
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgument(RuntimeException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    // NullPointer: bug interno
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(NullPointerException e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // Catch-All: para cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception e, HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, HttpStatus status, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}