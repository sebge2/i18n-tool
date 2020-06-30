package be.sgerard.test.i18n.mock;

import be.sgerard.i18n.service.event.ServerSentEventService;
import be.sgerard.i18n.service.security.session.UserLiveSessionManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastien Gerard
 */
@Primary
@Service
public class TestableServerSentEventService extends ServerSentEventService {

    private final List<ServerSentEventService.Event<?>> events = new ArrayList<>();

    public TestableServerSentEventService(UserLiveSessionManager userSessionManager) {
        super(userSessionManager);
    }

    public List<Event<?>> getAllEmittedEvents() {
        return events;
    }

    @Override
    protected Mono<Void> emit(Event<Object> event) {
        events.add(event);

        return super.emit(event);
    }
}
