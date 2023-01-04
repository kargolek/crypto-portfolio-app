package pl.cryptoportfolioapp.cryptopriceservice.validation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CryptocurrencyNotFoundException.class)
    public ResponseEntity<?> handleCryptoNotFoundException(CryptocurrencyNotFoundException ex) {
        var jsonApiError = JsonApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .message(ex.getMessage())
                .build();
        log.info(String.format("Status: %s Message: %s", jsonApiError.getStatus(), jsonApiError.getMessage()));
        return new ResponseEntity<>(jsonApiError, jsonApiError.getStatus());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {

        var jsonApiError = JsonApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        log.info(String.format("Status: %s Message: %s", jsonApiError.getStatus(), jsonApiError.getMessage()));
        return new ResponseEntity<>(jsonApiError, jsonApiError.getStatus());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        var jsonApiError = JsonApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        log.info(String.format("Status: %s Message: %s", jsonApiError.getStatus(), jsonApiError.getMessage()));
        return new ResponseEntity<>(jsonApiError, jsonApiError.getStatus());
    }

    @Override
    public @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex,
                                                                        @NotNull HttpHeaders headers,
                                                                        @NotNull HttpStatus status,
                                                                        @NotNull WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        var jsonApiError = JsonApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Method argument are not valid")
                .errors(errors)
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        log.info(String.format("Status: %s Message: %s Errors: %s",
                jsonApiError.getStatus(),
                jsonApiError.getMessage(),
                jsonApiError.getErrors()));
        return new ResponseEntity<>(jsonApiError, jsonApiError.getStatus());
    }
}
