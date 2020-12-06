package be.sgerard.i18n.service;

import be.sgerard.i18n.model.support.LocalizedMessageHolder;

/**
 * Exception thrown when the caller made a bad request.
 *
 * @author Sebastien Gerard
 */
public class BadRequestException extends RuntimeException implements LocalizedMessageHolder {

    public static BadRequestException cannotParseException(String content, Throwable cause) {
        return new BadRequestException("The content [" + content + "] cannot be parsed.", "BadRequestException.cannot-parse.message", null, content);
    }

    public static BadRequestException idRequestNotMatchIdBodyException(String idRequest, String idBody) {
        return new BadRequestException(
                String.format("The id %s does not match the id in the body %s.", idRequest, idBody),
                "BadRequestException.id-request-id-body-not-match.message",
                null,
                idRequest, idBody
        );
    }

    public static BadRequestException missingHeader(String headerName) {
        return new BadRequestException("The header [" + headerName + "] is missing.", "BadRequestException.missing-header.message", null, headerName);
    }

    public static BadRequestException missingReviewMessage() {
        return new BadRequestException("There is no message specify. A message is needed when starting a review.", "BadRequestException.start-review-no-message.message", null);
    }

    public static BadRequestException missingFilePart() {
        return new BadRequestException("There is no file part in your multi-form-data request.", "BadRequestException.no-file-part.message", null);
    }

    public static BadRequestException unexpectedFormPart() {
        return new BadRequestException("The multi-form-data has not the expected fields. Please check your request.", "BadRequestException.wrong-multi-form-data.message", null);
    }

    private final String messageKey;
    private final Object[] parameters;

    public BadRequestException(String message, String messageKey, Throwable cause, Object... parameters) {
        super(message, cause);

        this.messageKey = messageKey;
        this.parameters = parameters;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public Object[] getMessageParameters() {
        return parameters;
    }
}
