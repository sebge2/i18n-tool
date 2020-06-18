package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.dto.RepositorySummaryDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.*;

/**
 * {@link RepositoryListener Listener} broadcasting events.
 *
 * @author Sebastien Gerard
 */
@Component
public class RepositoryEventListener implements RepositoryListener<RepositoryEntity> {

    private final EventService eventService;

    public RepositoryEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<Void> onCreate(RepositoryEntity repository) {
        return this.eventService.broadcastEvent(ADDED_REPOSITORY, RepositorySummaryDto.builder(repository).build());
    }

    @Override
    public Mono<Void> onUpdate(RepositoryEntity repository) {
        return this.eventService.broadcastEvent(UPDATED_REPOSITORY, RepositorySummaryDto.builder(repository).build());
    }

    @Override
    public Mono<Void> onDelete(RepositoryEntity repository) {
        return this.eventService.broadcastEvent(DELETED_REPOSITORY, RepositorySummaryDto.builder(repository).build());
    }
}
