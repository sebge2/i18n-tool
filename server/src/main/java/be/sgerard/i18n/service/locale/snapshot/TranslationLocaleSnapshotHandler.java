package be.sgerard.i18n.service.locale.snapshot;

import be.sgerard.i18n.model.i18n.dto.snapshot.TranslationLocaleSnapshotDto;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.i18n.TranslationLocaleRepository;
import be.sgerard.i18n.service.locale.validation.TranslationLocaleValidator;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import be.sgerard.i18n.service.snapshot.SnapshotHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link SnapshotHandler Snapshot handler} for {@link TranslationLocaleEntity translation locales}.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationLocaleSnapshotHandler extends BaseSnapshotHandler<TranslationLocaleEntity, TranslationLocaleSnapshotDto> {

    /**
     * Name of the file containing translation locales.
     */
    public static final String FILE = "translation-locale.json";

    private final TranslationLocaleValidator validator;

    public TranslationLocaleSnapshotHandler(TranslationLocaleRepository repository,
                                            TranslationLocaleValidator validator,
                                            ObjectMapper objectMapper) {
        super(FILE, TranslationLocaleSnapshotDto.class, objectMapper, repository);

        this.validator = validator;
    }

    @Override
    public int getImportPriority() {
        return 0;
    }

    @Override
    protected Mono<ValidationResult> validate(TranslationLocaleEntity locale) {
        return validator.beforePersist(locale);
    }

    @Override
    protected Mono<TranslationLocaleSnapshotDto> mapToDto(TranslationLocaleEntity locale) {
        return Mono.just(
                TranslationLocaleSnapshotDto.builder()
                        .id(locale.getId())
                        .displayName(locale.getDisplayName().orElse(null))
                        .icon(locale.getIcon())
                        .language(locale.getLanguage())
                        .region(locale.getRegion().orElse(null))
                        .variants(locale.getVariants())
                        .build()
        );
    }

    @Override
    protected Mono<TranslationLocaleEntity> mapFromDto(TranslationLocaleSnapshotDto dto) {
        return Mono.just(
                new TranslationLocaleEntity(
                        dto.getLanguage(),
                        dto.getRegion().orElse(null),
                        dto.getVariants(),
                        dto.getDisplayName().orElse(null),
                        dto.getIcon()
                ).setId(dto.getId())
        );
    }
}
