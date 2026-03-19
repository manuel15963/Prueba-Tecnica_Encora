package pe.com.interbank.infrastructure.adapter.input.rest;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ServerWebExchange;
import pe.com.interbank.domain.exception.InvalidCredentialException;
import pe.com.interbank.domain.exception.NotFoundException;
import pe.com.interbank.domain.exception.UserNotFoundException;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.ErrorResponse;
import pe.com.interbank.utils.Constants;
import pe.com.interbank.utils.StructuredLogUtil;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static pe.com.interbank.utils.ErrorCatalog.GENERIC_ERROR;
import static pe.com.interbank.utils.ErrorCatalog.INVALID_USER;
import static pe.com.interbank.utils.ErrorCatalog.NOT_FOUND;
import static pe.com.interbank.utils.ErrorCatalog.USER_DUPLICATE;
import static pe.com.interbank.utils.ErrorCatalog.USER_NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice  {

    @ExceptionHandler(DuplicateRequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistsException(DuplicateRequestException ex, ServerWebExchange exchange) {
        logException("http.error.duplicate", USER_DUPLICATE.getCode(), ex, exchange, false);
        return ErrorResponse.builder()
                .code(USER_DUPLICATE.getCode())
                .message(USER_DUPLICATE.getTitle())
                .details(Collections.singletonList(ex.getMessage()))
                .timestamp(Constants.convertLocalDateTimeToString(LocalDateTime.now()))
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException ex, ServerWebExchange exchange) {
        logException("http.error.notFound", NOT_FOUND.getCode(), ex, exchange, false);
        return ErrorResponse.builder()
                .code(NOT_FOUND.getCode())
                .message(NOT_FOUND.getTitle())
                .details(List.of(NOT_FOUND.getDescription()))
                .timestamp(Constants.convertLocalDateTimeToString(LocalDateTime.now()))
                .build();
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex, ServerWebExchange exchange) {
        logException("http.error.userNotFound", USER_NOT_FOUND.getCode(), ex, exchange, false);
        return ErrorResponse.builder()
                .code(USER_NOT_FOUND.getCode())
                .message(USER_NOT_FOUND.getTitle())
                .details(List.of(USER_NOT_FOUND.getDescription()))
                .timestamp(Constants.convertLocalDateTimeToString(LocalDateTime.now()))
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvalidCredentialException.class)
    public ErrorResponse handleInvalidCredentialException(InvalidCredentialException ex, ServerWebExchange exchange) {
        logException("http.error.invalidCredential", INVALID_USER.getCode(), ex, exchange, false);
        return ErrorResponse.builder()
                .code(INVALID_USER.getCode())
                .message(INVALID_USER.getTitle())
                .details(List.of(INVALID_USER.getDescription()))
                .timestamp(Constants.convertLocalDateTimeToString(LocalDateTime.now()))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            ServerWebExchange exchange) {
        BindingResult result = exception.getBindingResult();
        logException("http.error.validation", INVALID_USER.getCode(), exception, exchange, false);

        return ErrorResponse.builder()
                .code(INVALID_USER.getCode())
                .message(INVALID_USER.getTitle())
                .details(result.getFieldErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList())
                .timestamp(Constants.convertLocalDateTimeToString(LocalDateTime.now()))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
    public ErrorResponse handleConstraintViolationException(Exception exception, ServerWebExchange exchange) {
        List<String> details = exception instanceof ConstraintViolationException cve
                ? cve.getConstraintViolations().stream().map(violation -> violation.getMessage()).toList()
                : List.of(exception.getMessage());
        logException("http.error.constraint", INVALID_USER.getCode(), exception, exchange, false);

        return ErrorResponse.builder()
                .code(INVALID_USER.getCode())
                .message(INVALID_USER.getTitle())
                .details(details)
                .timestamp(Constants.convertLocalDateTimeToString(LocalDateTime.now()))
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericError(Exception exception, ServerWebExchange exchange) {
        logException("http.error.unexpected", GENERIC_ERROR.getCode(), exception, exchange, true);
        return ErrorResponse.builder()
                .code(GENERIC_ERROR.getCode())
                .message(GENERIC_ERROR.getTitle())
                .details(Collections.singletonList(exception.getMessage()))
                .timestamp(Constants.convertLocalDateTimeToString(LocalDateTime.now()))
                .build();
    }

    private void logException(String event, String errorCode, Exception ex, ServerWebExchange exchange, boolean asError) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("errorCode", errorCode);
        fields.put("exception", ex.getClass().getSimpleName());
        fields.put("message", ex.getMessage());
        fields.put("path", exchange.getRequest().getPath().value());
        fields.put("method", exchange.getRequest().getMethod() != null ? exchange.getRequest().getMethod().name() : null);
        fields.put("correlationId", exchange.getAttribute(Constants.CORRELATION_ID_ATTR));

        if (asError) {
            StructuredLogUtil.error(log, event, fields);
        } else {
            StructuredLogUtil.warn(log, event, fields);
        }
    }

}