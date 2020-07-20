package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.repository.i18n.TranslationLocaleRepository;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.i18n.listener.TranslationLocaleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final TranslationLocaleListener localeListener;

    public TranslationLocaleManagerImpl(TranslationLocaleRepository repository, TranslationLocaleListener localeListener) {
        this.repository = repository;
        this.localeListener = localeListener;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TranslationLocaleEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TranslationLocaleEntity> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Mono<TranslationLocaleEntity> create(TranslationLocaleCreationDto creationDto) {
        return Mono
                .just(new TranslationLocaleEntity(
                        creationDto.getLanguage(),
                        creationDto.getRegion().orElse(null),
                        creationDto.getVariants(),
                        creationDto.getDisplayName().orElse(null),
                        creationDto.getIcon()
                ))
                .flatMap(locale -> localeListener.beforePersist(locale)
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return locale;
                        })
                )
                .flatMap(repository::save)
                .flatMap(translationLocale ->
                        localeListener
                                .onCreatedLocale(translationLocale)
                                .thenReturn(translationLocale)
                );
    }

    @Override
    @Transactional
    public Mono<TranslationLocaleEntity> update(TranslationLocaleDto localeDto) {
        return findById(localeDto.getId())
                .flatMap(locale -> localeListener.beforeUpdate(locale, localeDto)
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return locale;
                        })
                )
                .map(entity ->
                        entity
                                .setLanguage(localeDto.getLanguage())
                                .setRegion(localeDto.getRegion().orElse(null))
                                .setVariants(localeDto.getVariants())
                                .setIcon(localeDto.getIcon())
                                .setDisplayName(localeDto.getDisplayName().orElse(null))
                )
                .flatMap(repository::save)
                .flatMap(translationLocale ->
                        localeListener
                                .onUpdatedLocale(translationLocale)
                                .thenReturn(translationLocale)
                );
    }

    @Override
    @Transactional
    public Mono<TranslationLocaleEntity> delete(String localeId) {
        return findById(localeId)
                .flatMap(translationLocale -> localeListener.beforeDelete(translationLocale)
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return translationLocale;
                        })
                )
                .flatMap(translationLocale ->
                        localeListener
                                .onDeletedLocale(translationLocale)
                                .thenReturn(translationLocale)
                )
                .flatMap(translationLocale ->
                        repository
                                .delete(translationLocale)
                                .thenReturn(translationLocale)
                );
    }

}
