package be.sgerard.i18n.service.workspace.snapshot;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntryEntity;
import be.sgerard.i18n.model.i18n.snapshot.BundleFileEntrySnapshotDto;
import be.sgerard.i18n.model.i18n.snapshot.BundleFileSnapshotDto;
import be.sgerard.i18n.model.i18n.snapshot.BundleType;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.persistence.AbstractReviewEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.model.workspace.snapshot.AbstractReviewSnapshotDto;
import be.sgerard.i18n.model.workspace.snapshot.WorkspaceSnapshotDto;
import be.sgerard.i18n.repository.workspace.WorkspaceRepository;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import be.sgerard.i18n.service.workspace.validation.WorkspaceValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * {@link BaseSnapshotHandler Snapshot} handler for {@link WorkspaceEntity workspaces}
 *
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceSnapshotHandler extends BaseSnapshotHandler<WorkspaceEntity, WorkspaceSnapshotDto> {

    /**
     * Name of the file containing workspaces.
     */
    public static final String FILE = "workspace.json";

    private final WorkspaceValidator validator;
    private final List<ReviewSnapshotDtoMapper<AbstractReviewEntity, AbstractReviewSnapshotDto>> reviewMappers;

    @SuppressWarnings("unchecked")
    protected WorkspaceSnapshotHandler(ObjectMapper objectMapper,
                                       WorkspaceRepository repository,
                                       WorkspaceValidator validator,
                                       List<ReviewSnapshotDtoMapper<?, ?>> reviewMappers) {
        super(FILE, WorkspaceSnapshotDto.class, objectMapper, repository);

        this.validator = validator;
        this.reviewMappers = (List<ReviewSnapshotDtoMapper<AbstractReviewEntity, AbstractReviewSnapshotDto>>) (List<?>) reviewMappers;
    }

    @Override
    public int getImportPriority() {
        return 30;
    }

    @Override
    protected Mono<ValidationResult> validate(WorkspaceEntity workspace) {
        return validator.beforePersist(workspace);
    }

    @Override
    protected Mono<WorkspaceEntity> mapFromDto(WorkspaceSnapshotDto dto) {
        final Function<AbstractReviewEntity, WorkspaceEntity> mapper = (review) -> new WorkspaceEntity(dto.getRepository(), dto.getBranch())
                .setId(dto.getId())
                .setStatus(mapFromDto(dto.getStatus()))
                .setFiles(dto.getFiles().stream().map(this::mapFromDto).collect(toList()))
                .setLastSynchronization(dto.getLastSynchronization().orElse(null))
                .setReview(review);

        return Mono
                .justOrEmpty(dto.getReview())
                .flatMap(this::mapFromDto)
                .map(mapper)
                .switchIfEmpty(Mono.fromCallable(() -> mapper.apply(null)));
    }

    @Override
    protected Mono<WorkspaceSnapshotDto> mapToDto(WorkspaceEntity workspace) {
        final Function<AbstractReviewSnapshotDto, WorkspaceSnapshotDto> mapper = (review) -> WorkspaceSnapshotDto.builder()
                .id(workspace.getId())
                .repository(workspace.getRepository())
                .branch(workspace.getBranch())
                .status(mapToDto(workspace.getStatus()))
                .files(workspace.getFiles().stream().map(this::mapToDto).collect(toList()))
                .lastSynchronization(workspace.getLastSynchronization().orElse(null))
                .review(review)
                .build();

        return Mono
                .justOrEmpty(workspace.getReview())
                .flatMap(this::mapToDto)
                .map(mapper)
                .switchIfEmpty(Mono.fromCallable(() -> mapper.apply(null)));
    }

    /**
     * Maps the status from its DTO representation.
     */
    private WorkspaceStatus mapFromDto(WorkspaceSnapshotDto.WorkspaceStatus status) {
        switch (status) {
            case INITIALIZED:
                return WorkspaceStatus.INITIALIZED;
            case NOT_INITIALIZED:
                return WorkspaceStatus.NOT_INITIALIZED;
            case IN_REVIEW:
                return WorkspaceStatus.IN_REVIEW;
            default:
                throw new UnsupportedOperationException("Unsupported status [" + status + "].");
        }
    }

    /**
     * Maps the bundle file from its DTO representation.
     */
    private BundleFileEntity mapFromDto(BundleFileSnapshotDto dto) {
        return new BundleFileEntity(
                dto.getName(),
                dto.getLocation(),
                mapFromDto(dto.getType()),
                dto.getFiles().stream().map(this::mapFromDto).collect(toList())
        )
                .setId(dto.getId())
                .setNumberKeys(dto.getNumberKeys());
    }

    /**
     * Maps the type to from DTO representation.
     */
    private be.sgerard.i18n.model.i18n.BundleType mapFromDto(BundleType bundleType) {
        switch (bundleType) {
            case JAVA_PROPERTIES:
                return be.sgerard.i18n.model.i18n.BundleType.JAVA_PROPERTIES;
            case JSON_ICU:
                return be.sgerard.i18n.model.i18n.BundleType.JSON_ICU;
            default:
                throw new UnsupportedOperationException("Unsupported bundle type [" + bundleType + "].");
        }
    }

    /**
     * Maps the file entry from its DTO representation.
     */
    private BundleFileEntryEntity mapFromDto(BundleFileEntrySnapshotDto dto) {
        return new BundleFileEntryEntity(dto.getLocale(), dto.getFile())
                .setId(dto.getId());
    }

    /**
     * Maps the review from its DTO representation.
     */
    private Mono<AbstractReviewEntity> mapFromDto(AbstractReviewSnapshotDto dto) {
        return Flux
                .fromIterable(reviewMappers)
                .filter(mapper -> mapper.support(dto))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported dto [" + dto + "]. Hint: check that all mappers have been registered.")))
                .flatMap(mapper -> mapper.mapFromDto(dto));
    }

    /**
     * Maps the status to its DTO representation.
     */
    private WorkspaceSnapshotDto.WorkspaceStatus mapToDto(WorkspaceStatus status) {
        switch (status) {
            case INITIALIZED:
                return WorkspaceSnapshotDto.WorkspaceStatus.INITIALIZED;
            case NOT_INITIALIZED:
                return WorkspaceSnapshotDto.WorkspaceStatus.NOT_INITIALIZED;
            case IN_REVIEW:
                return WorkspaceSnapshotDto.WorkspaceStatus.IN_REVIEW;
            default:
                throw new UnsupportedOperationException("Unsupported status [" + status + "].");
        }
    }

    /**
     * Maps the bundle file to its DTO representation.
     */
    private BundleFileSnapshotDto mapToDto(BundleFileEntity bundleFile) {
        return BundleFileSnapshotDto.builder()
                .id(bundleFile.getId())
                .name(bundleFile.getName())
                .location(bundleFile.getLocation())
                .type(mapToDto(bundleFile.getType()))
                .numberKeys(bundleFile.getNumberKeys())
                .files(bundleFile.getFiles().stream().map(this::mapToDto).collect(toSet()))
                .build();
    }

    /**
     * Maps the type to its DTO representation.
     */
    private BundleType mapToDto(be.sgerard.i18n.model.i18n.BundleType bundleType) {
        switch (bundleType) {
            case JAVA_PROPERTIES:
                return BundleType.JAVA_PROPERTIES;
            case JSON_ICU:
                return BundleType.JSON_ICU;
            default:
                throw new UnsupportedOperationException("Unsupported bundle type [" + bundleType + "].");
        }
    }

    /**
     * Maps the bundle file entry to its DTO representation.
     */
    private BundleFileEntrySnapshotDto mapToDto(BundleFileEntryEntity bundleFileEntry) {
        return BundleFileEntrySnapshotDto.builder()
                .id(bundleFileEntry.getId())
                .locale(bundleFileEntry.getLocale())
                .file(bundleFileEntry.getFile())
                .build();
    }

    /**
     * Maps the review to its DTO representation.
     */
    private Mono<AbstractReviewSnapshotDto> mapToDto(AbstractReviewEntity review) {
        return Flux
                .fromIterable(reviewMappers)
                .filter(mapper -> mapper.support(review))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported review [" + review + "]. Hint: check that all mappers have been registered.")))
                .flatMap(mapper -> mapper.mapToDto(review));
    }
}
