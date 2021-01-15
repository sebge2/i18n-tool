package be.sgerard.i18n.service.snapshot.listener;

import be.sgerard.i18n.model.snapshot.SnapshotEntity;
import be.sgerard.i18n.model.snapshot.dto.SnapshotDto;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.ADDED_SNAPSHOT;
import static be.sgerard.i18n.model.event.EventType.DELETED_SNAPSHOT;

/**
 * {@link SnapshotListener Listener} broadcasting events.
 *
 * @author Sebastien Gerard
 */
@Component
public class SnapshotEventListener implements SnapshotListener {

    private final EventService eventService;

    public SnapshotEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Mono<Void> afterPersist(SnapshotEntity snapshot) {
        return this.eventService.broadcastEvent(ADDED_SNAPSHOT, SnapshotDto.builder(snapshot).build());
    }

    @Override
    public Mono<Void> afterDelete(SnapshotEntity snapshot) {
        return this.eventService.broadcastEvent(DELETED_SNAPSHOT, SnapshotDto.builder(snapshot).build());
    }
}
