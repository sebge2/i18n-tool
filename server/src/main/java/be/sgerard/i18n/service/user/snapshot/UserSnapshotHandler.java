package be.sgerard.i18n.service.user.snapshot;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.user.snapshot.UserSnapshotDto;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.user.UserRepository;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import be.sgerard.i18n.service.snapshot.SnapshotHandler;
import be.sgerard.i18n.service.user.UserManager;
import be.sgerard.i18n.service.user.validator.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * {@link SnapshotHandler Snapshot handler} for {@link UserEntity users}.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserSnapshotHandler extends BaseSnapshotHandler<UserEntity, UserSnapshotDto> {

    /**
     * Name of the file containing users.
     */
    public static final String FILE = "user.json";

    /**
     * Validation message key specifying that there is no admin user.
     */
    public static final String MISSING_ADMIN = "validation.snapshot.user.missing-admin";

    private final UserValidator userValidator;
    private final List<UserSnapshotDtoMapper<UserEntity, UserSnapshotDto>> mappers;

    @SuppressWarnings("unchecked")
    public UserSnapshotHandler(UserRepository repository,
                               UserValidator userValidator,
                               ObjectMapper objectMapper,
                               List<UserSnapshotDtoMapper<?, ?>> mappers) {
        super(FILE, UserSnapshotDto.class, objectMapper, repository);

        this.userValidator = userValidator;
        this.mappers = (List<UserSnapshotDtoMapper<UserEntity, UserSnapshotDto>>) (List<?>) mappers;
    }

    @Override
    public int getImportPriority() {
        return 10;
    }

    @Override
    protected Mono<ValidationResult> validate(UserEntity user) {
        return userValidator.beforePersist(user);
    }

    @Override
    protected Flux<ValidationResult> validateAll(File snapshotFile) {
        return loadAll(snapshotFile)
                .filter(user -> Objects.equals(user.getUsername(), UserManager.ADMIN_USER_NAME))
                .hasElements()
                .flatMapMany(adminPresent ->
                        adminPresent
                                ? Flux.just(ValidationResult.EMPTY)
                                : Flux.just(ValidationResult.singleMessage(new ValidationMessage(MISSING_ADMIN)))
                );
    }

    @Override
    protected Mono<UserEntity> mapFromDto(UserSnapshotDto dto) {
        return Flux
                .fromIterable(mappers)
                .filter(mapper -> mapper.support(dto))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported dto [" + dto.getType() + "]. Hint: check that all mappers have been registered.")))
                .flatMap(mapper -> mapper.mapFromDto(dto));
    }

    @Override
    protected Mono<UserSnapshotDto> mapToDto(UserEntity user) {
        return Flux
                .fromIterable(mappers)
                .filter(mapper -> mapper.support(user))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported user [" + user.getClass() + "]. Hint: check that all mappers have been registered.")))
                .flatMap(mapper -> mapper.mapToDto(user));
    }
}
