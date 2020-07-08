package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.event.EventDto;
import be.sgerard.i18n.service.event.EventService;
import io.swagger.v3.oas.annotations.Operation;
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
    @GetMapping(value = "/event", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @Operation(summary = "Returns all incoming events.")
    public Flux<EventDto<Object>> getEvents() {
        return eventService.getEvents();
    }
}
