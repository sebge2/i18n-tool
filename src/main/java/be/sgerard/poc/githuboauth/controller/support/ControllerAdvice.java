package be.sgerard.poc.githuboauth.controller.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;

/**
 * @author Sebastien Gerard
 */
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    public ControllerAdvice() {
    }

    @MessageExceptionHandler
    @SendToUser("/user/event/errors")
    public String handleException(Throwable exception) {
        logger.debug("Exception occurred when processing STOMP message.", exception);

        return exception.getMessage();
    }
}
