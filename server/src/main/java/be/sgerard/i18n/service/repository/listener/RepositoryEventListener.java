package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.repository.RepositoryDtoMapper;
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
    private final RepositoryDtoMapper dtoMapper;

    public RepositoryEventListener(EventService eventService, RepositoryDtoMapper dtoMapper) {
        this.eventService = eventService;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<Void> onCreate(RepositoryEntity repository) {
        return this.eventService.broadcastEvent(ADDED_REPOSITORY, dtoMapper.mapToDto(repository));
    }

    @Override
    public Mono<Void> onInitialize(RepositoryEntity repository) {
        return this.eventService.broadcastEvent(UPDATED_REPOSITORY, dtoMapper.mapToDto(repository));
    }

    @Override
    public Mono<Void> onInitializationError(RepositoryEntity repository, Throwable error) {
        return this.eventService.broadcastEvent(UPDATED_REPOSITORY, dtoMapper.mapToDto(repository));
    }

    @Override
    public Mono<Void> onUpdate(RepositoryPatchDto patch, RepositoryEntity repository) {
        return this.eventService.broadcastEvent(UPDATED_REPOSITORY, dtoMapper.mapToDto(repository));
    }

    @Override
    public Mono<Void> onDelete(RepositoryEntity repository) {
        return this.eventService.broadcastEvent(DELETED_REPOSITORY, dtoMapper.mapToDto(repository));
    }
}
