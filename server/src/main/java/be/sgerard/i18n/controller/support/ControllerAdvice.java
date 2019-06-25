package be.sgerard.i18n.controller.support;

import be.sgerard.i18n.service.LockTimeoutException;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.git.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Sebastien Gerard
 */
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    public ControllerAdvice() {
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public Object handleIllegalStateException(IllegalStateException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RepositoryException.class)
    public Object handleRepositoryException(RepositoryException exception) {
        if (logger.isWarnEnabled()) {
            logger.warn(exception.getMessage(), exception);
        }

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = LockTimeoutException.class)
    public Object handleLockTimeoutException(LockTimeoutException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ValidationException.class)
    public Object handleValidationException(ValidationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public String handleBadCredentialsException() {
        return "forward:/login";
    }

    @MessageExceptionHandler
    @SendToUser("/user/event/errors")
    @SuppressWarnings("unused")
    public String handleException(Throwable exception) {
        logger.debug("Exception occurred when processing STOMP message.", exception);

        return exception.getMessage();
    }
}
