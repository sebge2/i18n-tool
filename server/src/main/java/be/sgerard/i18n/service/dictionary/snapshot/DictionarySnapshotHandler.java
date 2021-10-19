package be.sgerard.i18n.service.dictionary.snapshot;

import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import be.sgerard.i18n.model.dictionary.snapshot.DictionaryEntrySnapshotDto;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.dictionary.DictionaryEntryRepository;
import be.sgerard.i18n.service.dictionary.validation.DictionaryValidator;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link BaseSnapshotHandler Snapshot handler} for {@link DictionaryEntryEntity dictionary entries}.
 *
 * @author Sebastien Gerard
 */
@Component
public class DictionarySnapshotHandler extends BaseSnapshotHandler<DictionaryEntryEntity, DictionaryEntrySnapshotDto> {

    /**
     * Name of the file containing the dictionary.
     */
    public static final String FILE = "dictionary.json";

    private final DictionaryValidator validator;

    public DictionarySnapshotHandler(ObjectMapper objectMapper,
                                     DictionaryEntryRepository repository,
                                     DictionaryValidator validator) {
        super(FILE, DictionaryEntrySnapshotDto.class, objectMapper, repository);
        this.validator = validator;
    }

    @Override
    public int getImportPriority() {
        return 60;
    }

    @Override
    protected Mono<ValidationResult> validate(DictionaryEntryEntity entity) {
        return validator.beforePersist(entity);
    }

    @Override
    protected Mono<DictionaryEntryEntity> mapFromDto(DictionaryEntrySnapshotDto dto) {
        return Mono.just(
                new DictionaryEntryEntity(dto.getTranslations()).setId(dto.getId())
        );
    }

    @Override
    protected Mono<DictionaryEntrySnapshotDto> mapToDto(DictionaryEntryEntity entity) {
        return Mono.just(
                new DictionaryEntrySnapshotDto(entity.getId(), entity.getTranslations())
        );
    }
}
