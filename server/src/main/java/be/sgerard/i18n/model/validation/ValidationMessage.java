package be.sgerard.i18n.model.validation;

import java.util.List;

import static java.util.Arrays.asList;


/**
 * @author Sebastien Gerard
 */
public class ValidationMessage {

    private final String messageKey;
    private final List<String> messageParameters;

    public ValidationMessage(String messageKey, List<String> messageParameters) {
        this.messageKey = messageKey;
        this.messageParameters = List.copyOf(messageParameters);
    }

    public ValidationMessage(String messageKey, String... messageParameters) {
        this(messageKey, asList(messageParameters));
    }

    public String getMessageKey() {
        return messageKey;
    }

    public List<String> getMessageParameters() {
        return messageParameters;
    }
}
