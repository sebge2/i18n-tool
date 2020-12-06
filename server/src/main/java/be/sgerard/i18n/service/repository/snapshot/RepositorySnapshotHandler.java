package be.sgerard.i18n.service.repository.snapshot;

import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.repository.snapshot.RepositorySnapshotDto;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.repository.RepositoryEntityRepository;
import be.sgerard.i18n.service.repository.RepositoryHandler;
import be.sgerard.i18n.service.repository.validation.RepositoryValidator;
import be.sgerard.i18n.service.security.repository.RepositoryCredentialsManager;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * {@link BaseSnapshotHandler Snapshot handler} for {@link RepositoryEntity repositories}.
 *
 * @author Sebastien Gerard
 */
@Component
public class RepositorySnapshotHandler extends BaseSnapshotHandler<RepositoryEntity, RepositorySnapshotDto> {

    /**
     * Name of the file containing repositories.
     */
    public static final String FILE = "repository.json";

    private final RepositoryValidator<RepositoryEntity> validator;
    private final RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto, RepositoryCredentials> handler;
    private final RepositoryCredentialsManager credentialsManager;
    private final List<RepositorySnapshotDtoMapper<RepositoryEntity, RepositorySnapshotDto>> mappers;

    @SuppressWarnings("unchecked")
    protected RepositorySnapshotHandler(ObjectMapper objectMapper,
                                        RepositoryEntityRepository repository,
                                        RepositoryValidator<RepositoryEntity> validator,
                                        RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto, RepositoryCredentials> handler,
                                        RepositoryCredentialsManager credentialsManager,
                                        List<RepositorySnapshotDtoMapper<?, ?>> mappers) {
        super(FILE, RepositorySnapshotDto.class, objectMapper, repository);

        this.validator = validator;
        this.handler = handler;
        this.credentialsManager = credentialsManager;
        this.mappers = (List<RepositorySnapshotDtoMapper<RepositoryEntity, RepositorySnapshotDto>>) (List<?>) mappers;
    }

    @Override
    public int getImportPriority() {
        return 30;
    }

    @Override
    protected Mono<ValidationResult> validate(RepositoryEntity repository) {
        return validator.beforePersist(repository);
    }

    @Override
    protected Mono<RepositoryEntity> beforeSave(RepositoryEntity repository) {
        return initializeRepository(repository);
    }

    @Override
    protected Mono<RepositoryEntity> beforeDelete(RepositoryEntity repository) {
        return deleteRepository(repository);
    }

    @Override
    protected Mono<RepositoryEntity> mapFromDto(RepositorySnapshotDto dto) {
        return Flux
                .fromIterable(mappers)
                .filter(mapper -> mapper.support(dto))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported dto [" + dto + "]. Hint: check that all mappers have been registered.")))
                .flatMap(mapper -> mapper.mapFromDto(dto));
    }

    @Override
    protected Mono<RepositorySnapshotDto> mapToDto(RepositoryEntity repository) {
        return Flux
                .fromIterable(mappers)
                .filter(mapper -> mapper.support(repository))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported repository [" + repository + "]. Hint: check that all mappers have been registered.")))
                .flatMap(mapper -> mapper.mapToDto(repository));
    }

    /**
     * Initializes the specified {@link RepositoryEntity repository} using the {@link RepositoryHandler handler}.
     */
    private Mono<RepositoryEntity> initializeRepository(RepositoryEntity repository) {
        return credentialsManager
                .loadUserCredentialsOrDie(repository)
                .flatMap(credentials -> handler.initializeRepository(repository, credentials));
    }

    /**
     * Deletes the specified {@link RepositoryEntity repository} using the {@link RepositoryHandler handler}.
     */
    private Mono<RepositoryEntity> deleteRepository(RepositoryEntity repository) {
        return credentialsManager
                .loadUserCredentialsOrDie(repository)
                .flatMap(credentials -> handler.deleteRepository(repository, credentials));
    }
}
