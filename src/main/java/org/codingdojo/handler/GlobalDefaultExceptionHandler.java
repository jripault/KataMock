package org.codingdojo.handler;

import org.codingdojo.exception.NoSundayRuleException;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.exception.TaskNotAssignableException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ControllerAdvice
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            NoSundayRuleException.class,
            TaskNotAssignableException.class
    })
    public ResponseEntity<Object> handleBusinessException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof NoSundayRuleException || ex instanceof TaskNotAssignableException) {
            status = HttpStatus.BAD_REQUEST;
        }

        return handleExceptionInternal(ex, null, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = {
            EntityNotFoundException.class,
            ObjectNotFoundException.class,
            ResourceNotFoundException.class,
            DataIntegrityViolationException.class,
            EntityExistsException.class,
            OptimisticLockingFailureException.class,
            EmptyResultDataAccessException.class
    })
    public ResponseEntity<Object> handleJpaException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof EntityNotFoundException || ex instanceof ObjectNotFoundException || ex instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof DataIntegrityViolationException || ex instanceof EntityExistsException || ex instanceof OptimisticLockingFailureException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof EmptyResultDataAccessException) {
            status = HttpStatus.BAD_REQUEST;
        }

        return handleExceptionInternal(ex, null, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = {
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            ValidationException.class
    })
    public ResponseEntity<Object> handleValidationException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof ConstraintViolationException || ex instanceof IllegalArgumentException || ex instanceof ValidationException) {
            status = HttpStatus.BAD_REQUEST;
        }

        return handleExceptionInternal(ex, null, new HttpHeaders(), status, request);
    }
}
