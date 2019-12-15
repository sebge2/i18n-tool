package be.sgerard.i18n.model.support;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Error messages that can be returned through the Web API.
 *
 * @author Sebastien Gerard
 */
public class ErrorMessages {

    private final String id;
    private final Instant time;
    private final List<String> messages;

    public ErrorMessages(List<String> messages) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.time = Instant.now();
        this.messages = messages;
    }

    /**
     * Returns the unique id of this message.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the time when this message has been built.
     */
    public Instant getTime() {
        return time;
    }

    /**
     * Returns all the (localized) messages.
     */
    public List<String> getMessages() {
        return messages;
    }
}
