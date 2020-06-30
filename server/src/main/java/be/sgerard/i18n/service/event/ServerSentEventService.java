package be.sgerard.i18n.service.event;

import be.sgerard.i18n.model.event.EventDto;
import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.session.UserLiveSessionManager;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * {@link EventService Event service} based on the Server-side-events.
 *
 * @author Sebastien Gerard
 */
@Service
public class ServerSentEventService implements EventService {

    private final UserLiveSessionManager userSessionManager;
    private final EmitterProcessor<Event<Object>> emitter;
    private final FluxSink<Event<Object>> sink;

    @Lazy
    public ServerSentEventService(UserLiveSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
        this.emitter = EmitterProcessor.create();
        this.sink = emitter.sink();
    }

    @Override
    public Mono<Void> broadcastEvent(EventType eventType, Object payload) {
        return emit(new Event<>(eventType, payload, userLiveSession -> userLiveSession.getSessionRoles().contains(UserRole.MEMBER_OF_REPOSITORY)));
    }

    @Override
    public Mono<Void> sendEventToUsers(UserRole userRole, EventType eventType, Object payload) {
        return emit(new Event<>(eventType, payload, userLiveSession -> userLiveSession.getSessionRoles().contains(userRole)));
    }

    @Override
    public Mono<Void> sendEventToUser(UserDto user, EventType eventType, Object payload) {
        return emit(new Event<>(eventType, payload, userLiveSession -> Objects.equals(userLiveSession.getUserId(), user.getId())));
    }

    @Override
    public Mono<Void> sendEventToSession(String sessionId, EventType eventType, Object payload) {
        return emit(new Event<>(eventType, payload, userLiveSession -> Objects.equals(userLiveSession.getId(), sessionId)));
    }

    @Override
    public Flux<EventDto<Object>> getEvents() {
        return userSessionManager
                .startSession()
                .flatMapMany(userLiveSession ->
                        emitter
                                .flatMap(event ->
                                        userSessionManager
                                                .getSessionOrDie(userLiveSession.getId())
                                                .map(updatedUserLiveSession -> Pair.of(event, updatedUserLiveSession))
                                )
                                .filter(eventAndSession -> eventAndSession.getLeft().getEventFilter().test(eventAndSession.getRight()))
                                .map(Pair::getLeft)
                                .map(Event::toDto)
//                                .doAfterTerminate(() -> userSessionManager.stopSession(userLiveSession).subscribe()) TODO
                );
    }

    /**
     * Emits the specified {@link Event event}.
     */
    private Mono<Void> emit(Event<Object> event) {
        sink.next(event);

        return Mono.empty();
    }

    /**
     * Internal emitted event.
     */
    @Getter
    private final static class Event<D> {

        private final EventType type;
        private final D payload;
        private final Predicate<UserLiveSessionEntity> eventFilter;

        public Event(EventType type, D payload, Predicate<UserLiveSessionEntity> eventFilter) {
            this.type = type;
            this.payload = payload;
            this.eventFilter = eventFilter;
        }

        /**
         * Returns this event as a {@link EventDto DTO}.
         */
        public EventDto<D> toDto() {
            return new EventDto<>(getType(), getPayload());
        }
    }
}
