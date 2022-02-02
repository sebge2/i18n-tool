package be.sgerard.i18n.service.translator.snapshot;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.snapshot.ExternalTranslatorConfigSnapshotDto;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.dictionary.ExternalTranslatorConfigRepository;
import be.sgerard.i18n.service.translator.validation.ExternalTranslatorConfigValidator;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * {@link BaseSnapshotHandler Snapshot handler} for {@link ExternalTranslatorConfigEntity external translators}.
 *
 * @author Sebastien Gerard
 */
@Component
public class ExternalTranslatorConfigSnapshotHandler extends BaseSnapshotHandler<ExternalTranslatorConfigEntity, ExternalTranslatorConfigSnapshotDto> {

    /**
     * Name of the file containing configurations.
     */
    public static final String FILE = "external_translator_config.json";

    private final List<ExternalTranslatorConfigSnapshotDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigSnapshotDto>> mappers;
    private final ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity> validator;

    @SuppressWarnings("unchecked")
    public ExternalTranslatorConfigSnapshotHandler(ObjectMapper objectMapper,
                                                   ExternalTranslatorConfigRepository repository,
                                                   List<ExternalTranslatorConfigSnapshotDtoMapper<?, ?>> mappers,
                                                   ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity> validator) {
        super(FILE, ExternalTranslatorConfigSnapshotDto.class, objectMapper, repository);

        this.mappers = (List<ExternalTranslatorConfigSnapshotDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigSnapshotDto>>) (List<?>) mappers;
        this.validator = validator;
    }

    @Override
    public int getImportPriority() {
        return 60;
    }

    @Override
    protected Mono<ValidationResult> validate(ExternalTranslatorConfigEntity entity) {
        return validator.beforePersistOrUpdate(entity);
    }

    @Override
    protected Mono<ExternalTranslatorConfigEntity> mapFromDto(ExternalTranslatorConfigSnapshotDto dto) {
        return Flux
                .fromIterable(mappers)
                .filter(mapper -> mapper.support(dto))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported dto [" + dto + "]. Hint: check that all mappers have been registered.")))
                .flatMap(mapper -> mapper.mapFromDto(dto));
    }

    @Override
    protected Mono<ExternalTranslatorConfigSnapshotDto> mapToDto(ExternalTranslatorConfigEntity entity) {
        return Flux
                .fromIterable(mappers)
                .filter(mapper -> mapper.support(entity))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported configuration [" + entity + "]. Hint: check that all mappers have been registered.")))
                .flatMap(mapper -> mapper.mapToDto(entity));
    }
}
