package be.sgerard.i18n.model.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Application event notifying that an object changed.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "Event", description = "Application event notifying that an event occurred")
@Getter
public class EventDto<D> {

    @Schema(description = "Type of event (help to indicate the payload type)", required = true)
    private final EventType type;

    @Schema(description = "Payload of this event.", required = true)
    private final D payload;

    public EventDto(EventType type, D payload) {
        this.type = type;
        this.payload = payload;
    }
}
