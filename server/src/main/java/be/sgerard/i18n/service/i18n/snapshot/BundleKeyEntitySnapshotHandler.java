package be.sgerard.i18n.service.i18n.snapshot;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;
import be.sgerard.i18n.model.i18n.snapshot.BundleKeySnapshotDto;
import be.sgerard.i18n.model.i18n.snapshot.BundleKeyTranslationModificationSnapshotDto;
import be.sgerard.i18n.model.i18n.snapshot.BundleKeyTranslationSnapshotDto;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * {@link BaseSnapshotHandler Snapshot handler} for {@link BundleKeyEntity bundle keys}.
 *
 * @author Sebastien Gerard
 */
@Component
public class BundleKeyEntitySnapshotHandler extends BaseSnapshotHandler<BundleKeyEntity, BundleKeySnapshotDto> {

    /**
     * Name of the file containing bundle keys.
     */
    public static final String FILE = "bundle-key.json";

    public BundleKeyEntitySnapshotHandler(ObjectMapper objectMapper,
                                          BundleKeyEntityRepository repository) {
        super(FILE, BundleKeySnapshotDto.class, objectMapper, repository);
    }

    @Override
    public int getImportPriority() {
        return 50;
    }

    @Override
    protected Mono<ValidationResult> validate(BundleKeyEntity entity) {
        return Mono.just(ValidationResult.EMPTY);
    }

    @Override
    protected Mono<BundleKeyEntity> mapFromDto(BundleKeySnapshotDto dto) {
        return Mono.just(
                new BundleKeyEntity(dto.getWorkspace(), dto.getBundleFile(), dto.getKey())
                        .setId(dto.getId())
                        .setSortingKey(dto.getSortingKey())
                        .setTranslations(
                                dto.getTranslations().entrySet().stream()
                                        .collect(toMap(Map.Entry::getKey, this::mapFromDto))
                        )
        );
    }

    @Override
    protected Mono<BundleKeySnapshotDto> mapToDto(BundleKeyEntity bundleKey) {
        return Mono.just(
                BundleKeySnapshotDto.builder()
                        .id(bundleKey.getId())
                        .bundleFile(bundleKey.getBundleFile())
                        .key(bundleKey.getKey())
                        .workspace(bundleKey.getWorkspace())
                        .sortingKey(bundleKey.getSortingKey())
                        .translations(
                                bundleKey.getTranslations().entrySet().stream()
                                        .collect(toMap(Map.Entry::getKey, this::mapToDto))
                        )
                        .build()
        );
    }

    /**
     * Maps the bundle key translation from its DTO representation.
     */
    private BundleKeyTranslationEntity mapFromDto(Map.Entry<String, BundleKeyTranslationSnapshotDto> entryDto) {
        final BundleKeyTranslationSnapshotDto dto = entryDto.getValue();

        return new BundleKeyTranslationEntity(
                dto.getLocale(),
                dto.getOriginalValue().orElse(null),
                dto.getIndex()
        )
                .setModification(dto.getModification().map(this::mapFromDto).orElse(null));
    }

    /**
     * Maps the modification translation from its DTO representation.
     */
    private BundleKeyTranslationModificationEntity mapFromDto(BundleKeyTranslationModificationSnapshotDto dto) {
        return new BundleKeyTranslationModificationEntity(dto.getUpdatedValue().orElse(null), dto.getLastEditor().orElse(null));
    }

    /**
     * Maps the bundle key translation to its DTO representation.
     */
    private BundleKeyTranslationSnapshotDto mapToDto(Map.Entry<String, BundleKeyTranslationEntity> translation) {
        return BundleKeyTranslationSnapshotDto.builder()
                .locale(translation.getValue().getLocale())
                .index(translation.getValue().getIndex())
                .originalValue(translation.getValue().getOriginalValue().orElse(null))
                .modification(translation.getValue().getModification().map(this::mapToDto).orElse(null))
                .build();
    }

    /**
     * Maps the entity to its DTO representation.
     */
    private BundleKeyTranslationModificationSnapshotDto mapToDto(BundleKeyTranslationModificationEntity modification) {
        return BundleKeyTranslationModificationSnapshotDto.builder()
                .updatedValue(modification.getUpdatedValue().orElse(null))
                .lastEditor(modification.getLastEditor().orElse(null))
                .build();
    }
}
