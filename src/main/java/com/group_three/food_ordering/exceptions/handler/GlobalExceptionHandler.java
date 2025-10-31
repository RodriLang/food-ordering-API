package com.group_three.food_ordering.exceptions.handler;

import com.group_three.food_ordering.exceptions.*;
import com.group_three.food_ordering.exceptions.responses.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Error de entidad no encontrada en la base de datos
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        log.warn("[{}] {}", e.getEntityName(), e.getMessage());
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request, null);
    }

    // Error de intento de modificación de un pago ya completado o cancelado
    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentStatus(InvalidPaymentStatusException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, request, null);
    }

    // Error de intento de modificación de una Orden ya en progreso
    @ExceptionHandler(OrderInProgressException.class)
    public ResponseEntity<ErrorResponse> handleOrderInProgress(OrderInProgressException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, request, null);
    }

    // Error de acceso denegado por lógica de negocio
    @ExceptionHandler(LogicalAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleLogicalAccessDenied(LogicalAccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request, null);
    }

    // Error de intento de registrar un usuario con un email ya existente
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyUsed(EmailAlreadyUsedException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request, null);
    }

    // Error de solicitar más productos que los disponibles
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, request, null);
    }

    // Error de contexto indeterminado cuando no se puede obtener el Food Venue
    @ExceptionHandler(MissingTenantContextException.class)
    public ResponseEntity<ErrorResponse> handleIndeterminateTenantContext(MissingTenantContextException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request, null);
    }

    // Error al almacenar imágenes con la librería Cloudinary
    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<ErrorResponse> handleCloudinaryException(CloudinaryException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request, null);
    }

    // Error al generar código qr
    @ExceptionHandler(QrCodeGeneratorException.class)
    public ResponseEntity<ErrorResponse> handleQrCodeGeneratorException(QrCodeGeneratorException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request, null);
    }

    // Error de usuario iniciando sesión en una mesa teniendo otra en curso
    @ExceptionHandler(UserSessionConflictException.class)
    public ResponseEntity<ErrorResponse> handleUserSessionConflict(UserSessionConflictException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, request, null);
    }

    @ExceptionHandler(InvalidDiningTableStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDiningTableStatus(InvalidDiningTableStatusException e, HttpServletRequest request) {
        String message;
        message = switch (e.getDiningTableStatus()) {
            case OUT_OF_SERVICE -> "La mesa está fuera de servicio.";
            case COMPLETE -> "La mesa está completa.";
            case WAITING_RESET -> "La mesa está esperando ser reiniciada por un administrador.";
            default -> e.getMessage();
        };
        return buildErrorResponse(message, HttpStatus.CONFLICT, request, null);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request, null);
    }

    @ExceptionHandler(InvalidInvitationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInvitation(InvalidInvitationException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request, null);
    }

    @ExceptionHandler(InactiveEmploymentStatusException.class)
    public ResponseEntity<ErrorResponse> handleInactiveEmploymentStatus(InactiveEmploymentStatusException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, request, e.getAppCode());
    }

    @ExceptionHandler(DuplicatedEmploymentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedEmployment(DuplicatedEmploymentException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, request, e.getAppCode());
    }

    // Errores de validación de DTO (@Valid fallidos)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request, null);
    }

    // JSON mal formado o tipo de datos incompatible en el request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request, null);
    }

    // IllegalArgument o IllegalState: cuando el flujo de la lógica falla
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgument(RuntimeException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request, null);
    }

    // NullPointer: bug interno
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(NullPointerException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request, null);
    }

    // Catch-All: para cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request, null);
    }

    // Error de acceso denegado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN, request, null);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request, String appCode) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                appCode
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}