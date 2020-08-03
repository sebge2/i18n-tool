package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.event.EventDto;
import be.sgerard.i18n.service.event.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * {@link RestController Rest controller} for application events.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "Event", description = "Controller for application events.")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Returns all the events that now occurs (hot event source).
     */
    @GetMapping(value = "/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Returns all incoming events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ServerSentEventDescriptor.class)))
    })
    public Flux<ServerSentEvent<EventDto<Object>>> getEvents() {
        return eventService
                .getEvents()
                .map(event -> ServerSentEvent.builder(event).build());
    }

    @Schema(name = "ServerSentEvent", description = "Application event notifying that an event occurred")
    private interface ServerSentEventDescriptor {

        @Schema(description = "The event payload containing the event itself.")
        EventDto<Object> getData();

    }
}
