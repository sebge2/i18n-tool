package be.sgerard.poc.githuboauth.controller.support;

import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
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

    @MessageExceptionHandler
    @SendToUser("/user/event/errors")
    public String handleException(Throwable exception) {
        logger.debug("Exception occurred when processing STOMP message.", exception);

        return exception.getMessage();
    }
}
