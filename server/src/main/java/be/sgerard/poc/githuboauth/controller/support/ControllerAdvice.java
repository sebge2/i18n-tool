package be.sgerard.poc.githuboauth.controller.support;

import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;
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
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public Object handleIllegalStateException(IllegalStateException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RepositoryException.class)
    public Object handleRepositoryException(RepositoryException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = LockTimeoutException.class)
    public Object handleLockTimeoutException(LockTimeoutException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public String handleBadCredentialsException() {
        // TODO if the token expired
        return "forward:/login";
    }

    @MessageExceptionHandler
    @SendToUser("/user/event/errors")
    public String handleException(Throwable exception) {
        logger.debug("Exception occurred when processing STOMP message.", exception);

        return exception.getMessage();
    }
}
