package be.sgerard.i18n.service.locale;

import be.sgerard.i18n.model.locale.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.locale.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.repository.i18n.TranslationLocaleRepository;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.locale.listener.TranslationLocaleListener;
import be.sgerard.i18n.service.locale.validation.TranslationLocaleValidator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of the {@link TranslationLocaleManager translation locale manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class TranslationLocaleManagerImpl implements TranslationLocaleManager {

    private final TranslationLocaleRepository repository;
    private final TranslationLocaleValidator localeValidator;
    private final TranslationLocaleListener localeListener;

    public TranslationLocaleManagerImpl(TranslationLocaleRepository repository,
                                        TranslationLocaleValidator localeValidator,
                                        TranslationLocaleListener localeListener) {
        this.repository = repository;
        this.localeValidator = localeValidator;
        this.localeListener = localeListener;
    }

    @Override
    public Mono<TranslationLocaleEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<TranslationLocaleEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<TranslationLocaleEntity> create(TranslationLocaleCreationDto creationDto) {
        return Mono
                .just(new TranslationLocaleEntity(
                        creationDto.getLanguage().toLowerCase(),
                        creationDto.getRegion().map(String::toUpperCase).orElse(null),
                        creationDto.getVariants(),
                        creationDto.getDisplayName().orElse(null),
                        creationDto.getIcon()
                ))
                .flatMap(locale -> localeValidator.beforePersist(locale)
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return locale;
                        })
                )
                .flatMap(repository::save)
                .flatMap(translationLocale -> localeListener.afterPersist(translationLocale).thenReturn(translationLocale));
    }

    @Override
    public Mono<TranslationLocaleEntity> update(TranslationLocaleDto localeDto) {
        return findById(localeDto.getId())
                .flatMap(locale -> localeValidator.beforeUpdate(locale, localeDto)
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return locale;
                        })
                )
                .map(entity ->
                        entity
                                .setLanguage(localeDto.getLanguage().toLowerCase())
                                .setRegion(localeDto.getRegion().map(String::toUpperCase).orElse(null))
                                .setVariants(localeDto.getVariants())
                                .setIcon(localeDto.getIcon())
                                .setDisplayName(localeDto.getDisplayName().orElse(null))
                )
                .flatMap(translationLocale -> localeListener.beforeUpdate(translationLocale).thenReturn(translationLocale))
                .flatMap(repository::save)
                .flatMap(translationLocale -> localeListener.afterUpdate(translationLocale).thenReturn(translationLocale));
    }

    @Override
    public Mono<TranslationLocaleEntity> delete(String localeId) {
        return findById(localeId)
                .flatMap(translationLocale -> localeValidator.beforeDelete(translationLocale)
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return translationLocale;
                        })
                )
                .flatMap(translationLocale -> localeListener.beforeDelete(translationLocale).thenReturn(translationLocale))
                .flatMap(translationLocale -> repository.delete(translationLocale).thenReturn(translationLocale))
                .flatMap(translationLocale -> localeListener.afterDelete(translationLocale).thenReturn(translationLocale));
    }
}
