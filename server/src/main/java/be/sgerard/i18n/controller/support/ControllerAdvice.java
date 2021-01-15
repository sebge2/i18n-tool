package be.sgerard.i18n.controller.support;

import be.sgerard.i18n.model.support.ErrorMessages;
import be.sgerard.i18n.model.support.LocalizedMessageHolder;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.UnauthorizedRequestException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.error.ErrorMessagesProvider;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.snapshot.SnapshotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebInputException;

import static java.util.Collections.emptyList;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice Controller advice} for the whole application.
 *
 * @author Sebastien Gerard
 */
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    private final ErrorMessagesProvider messagesProvider;

    public ControllerAdvice(ErrorMessagesProvider messagesProvider) {
        this.messagesProvider = messagesProvider;
    }

    /**
     * Handles the {@link ResourceNotFoundException resource not found exception}.
     */
    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handle(ResourceNotFoundException exception) {
        final ErrorMessages errorMessages = messagesProvider.map(exception);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Resource not found with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles the {@link ValidationException validation exception}.
     */
    @ExceptionHandler(value = ValidationException.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handle(ValidationException exception) {
        final ErrorMessages errorMessages = messagesProvider.map(exception.getValidationResult().getMessages());

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validation exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the {@link BadRequestException bad-request exception}.
     */
    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handle(BadRequestException exception) {
        final ErrorMessages errorMessages = messagesProvider.map(exception);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Bad request with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the {@link UnauthorizedRequestException unauthorized exception}.
     */
    @ExceptionHandler(value = UnauthorizedRequestException.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handle(UnauthorizedRequestException exception) {
        final ErrorMessages errorMessages = messagesProvider.map(exception);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Unauthorized request with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles the {@link RepositoryException repository exception}.
     */
    @ExceptionHandler(value = RepositoryException.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handle(RepositoryException exception) {
        final ErrorMessages errorMessages = messagesProvider.map(exception);

        logger.error(String.format("Repository exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);

        return new ResponseEntity<>(errorMessages, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles the {@link SnapshotException snapshot exception}.
     */
    @ExceptionHandler(value = SnapshotException.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handle(SnapshotException exception) {
        final ErrorMessages errorMessages = messagesProvider.map(exception);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Snapshot exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

//    /**
//     * Handles the {@link MissingServletRequestParameterException parameter exception}.
//     */
//    @ExceptionHandler(value = ResponseStatusException.class)
//    public ResponseEntity<ErrorMessages> handleMissingServletRequestParameterException(ResponseStatusException exception) {
////        final ErrorMessages errorMessages = messagesProvider.map(new LocalizedMessageHolder.Simple("MissingServletRequestParameterException.message", exception.getParameterName()));
////
////        if (logger.isDebugEnabled()) {
////            logger.debug(String.format("Missing parameter exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
////        }
//
//        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
//    }

//    /**
//     * Handles the {@link MissingServletRequestParameterException parameter exception}.
//     */
//    @ExceptionHandler(value = MissingServletRequestParameterException.class)
//    public ResponseEntity<ErrorMessages> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
//        final ErrorMessages errorMessages = messagesProvider.map(new LocalizedMessageHolder.Simple("MissingServletRequestParameterException.message", exception.getParameterName()));
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Missing parameter exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
//        }
//
//        return new ResponseEntity<>(errorMessages, HttpStatus.FORBIDDEN);
//    }

//    /**
//     * Handles the {@link ResponseStatusException parameter exception}.
//     */
//    @ExceptionHandler(value = ResponseStatusException.class)
//    public ResponseEntity<ErrorMessages> handleResponseStatusException(ResponseStatusException exception) {
//        // TODO issue-126
//        final ErrorMessages errorMessages = messagesProvider.map(new LocalizedMessageHolder.Simple("MissingServletRequestParameterException.message"));
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Missing parameter exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
//        }
//
//        return new ResponseEntity<>(errorMessages, exception.getStatus());
//    }

    /**
     * Handles the {@link MissingServletRequestParameterException access denied exception}.
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handle(AccessDeniedException exception) {
        final ErrorMessages errorMessages = messagesProvider.map(new LocalizedMessageHolder.Simple("AccessDeniedException.message"));

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Method not allowed exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.FORBIDDEN);
    }

//    /**
//     * Handles the {@link MethodNotAllowedException method-not-supported exception}.
//     */
//    @ExceptionHandler(value = MethodNotAllowedException.class)
//    public ResponseEntity<ErrorMessages> handle(MethodNotAllowedException exception) {
//        final ErrorMessages errorMessages = messagesProvider.map(new LocalizedMessageHolder.Simple("HttpRequestMethodNotSupportedException.message", exception.getHttpMethod()));
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Method not allowed exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);
//        }
//
//        return new ResponseEntity<>(errorMessages, HttpStatus.METHOD_NOT_ALLOWED);
//    }

    /**
     * Handles the {@link ServerWebInputException web-input exception}.
     */
    @ExceptionHandler(value = ServerWebInputException.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handleServerWebInputException(ServerWebInputException exception) {
        final ErrorMessages errorMessages;
        if (exception.getCause() instanceof DecodingException) {
            errorMessages = messagesProvider.map(new LocalizedMessageHolder.Simple("ServerWebInputException.DecodingException.message"));
        } else {
            errorMessages = messagesProvider.map(emptyList());
        }

        logger.error(String.format("Exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);

        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles any {@link Exception exception} that are not handled by the other handlers.
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus
    public ResponseEntity<ErrorMessages> handleException(Exception exception) {
        final ErrorMessages errorMessages = messagesProvider.map(new LocalizedMessageHolder.Simple("InternalException.message"));

        logger.error(String.format("Exception with id %s, message %s.", errorMessages.getId(), exception.getMessage()), exception);

        return new ResponseEntity<>(errorMessages, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public String handleBadCredentialsException() {
        return "forward:/login";
    }

}
