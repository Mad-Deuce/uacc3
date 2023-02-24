package dms.exception.handler;

import dms.exception.DeviceValidationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationErrors(ConstraintViolationException ex) {
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

    @ExceptionHandler(DeviceValidationException.class)
    public ResponseEntity<?> handleValidationErrors(DeviceValidationException ex) {
        return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoSuchFieldException.class)
    public ResponseEntity<?> handleNoSuchFieldException(NoSuchFieldException ex) {
        return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

