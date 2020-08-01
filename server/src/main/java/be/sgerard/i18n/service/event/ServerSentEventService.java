package be.sgerard.i18n.service.event;

import be.sgerard.i18n.model.event.EventDto;
import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
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
    private final AuthenticationManager authenticationManager;
    private final EmitterProcessor<Event<Object>> emitter;
    private final FluxSink<Event<Object>> sink;

    @Lazy
    public ServerSentEventService(UserLiveSessionManager userSessionManager,
                                  AuthenticationManager authenticationManager) {
        this.userSessionManager = userSessionManager;
        this.authenticationManager = authenticationManager;
        this.emitter = EmitterProcessor.create(false);
        this.sink = emitter.sink();
    }

    @Override
    public Mono<Void> broadcastEvent(EventType eventType, Object payload) {
        return emit(new Event<>(eventType, payload, currentAuthenticatedUser -> currentAuthenticatedUser.getSessionRoles().contains(UserRole.MEMBER_OF_ORGANIZATION)));
    }

    @Override
    public Mono<Void> sendEventToUsers(UserRole userRole, EventType eventType, Object payload) {
        return emit(new Event<>(eventType, payload, currentAuthenticatedUser -> currentAuthenticatedUser.getSessionRoles().contains(userRole)));
    }

    @Override
    public Mono<Void> sendEventToUser(UserDto user, EventType eventType, Object payload) {
        return emit(new Event<>(eventType, payload, currentAuthenticatedUser -> Objects.equals(currentAuthenticatedUser.getUserId(), user.getId())));
    }

    @Override
    public Mono<Void> sendEventToUser(AuthenticatedUser authenticatedUser, EventType eventType, Object payload) {
        return emit(new Event<>(eventType, payload, currentAuthenticatedUser -> Objects.equals(currentAuthenticatedUser.getId(), currentAuthenticatedUser.getId())));
    }

    @Override
    public Flux<EventDto<Object>> getEvents() {
        return userSessionManager
                .startSession()
                .flatMapMany(userLiveSession ->
                        emitter
                                .flatMap(event ->
                                        authenticationManager
                                                .getCurrentUserOrDie()
                                                .map(currentAuthenticatedUser -> Pair.of(event, currentAuthenticatedUser))
                                )
                                .filter(eventAndSession -> eventAndSession.getLeft().isVisible(eventAndSession.getRight()))
                                .map(Pair::getLeft)
                                .map(Event::toDto)
                                .doOnCancel(() -> userSessionManager.stopSession(userLiveSession).subscribe())
                                .doOnTerminate(() -> userSessionManager.stopSession(userLiveSession).subscribe())
                );
    }

    /**
     * Emits the specified {@link Event event}.
     */
    protected Mono<Void> emit(Event<Object> event) {
        sink.next(event);

        return Mono.empty();
    }

    /**
     * Internal emitted event.
     */
    @Getter
    protected final static class Event<D> {

        private final EventType type;
        private final D payload;
        private final Predicate<AuthenticatedUser> eventFilter;

        public Event(EventType type, D payload, Predicate<AuthenticatedUser> eventFilter) {
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

        /**
         * Returns whether the current event is visible by the specified user.
         */
        public boolean isVisible(AuthenticatedUser authenticatedUser){
            return eventFilter.test(authenticatedUser);
        }
    }
}
