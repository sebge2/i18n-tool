package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.repository.i18n.TranslationLocaleRepository;
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
        return Mono.justOrEmpty(repository.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TranslationLocaleEntity> findAll() {
        return Flux.fromIterable(repository.findAll());
    }

    @Override
    @Transactional
    public Mono<TranslationLocaleEntity> create(TranslationLocaleCreationDto creationDto) {
        return Mono
                .just(new TranslationLocaleEntity(
                        creationDto.getLanguage(),
                        creationDto.getRegion(),
                        creationDto.getVariants(),
                        creationDto.getIcon()
                ))
                .flatMap(locale -> localeListener.beforePersist(locale).thenReturn(locale))
                .map(repository::save)
                .map(translationLocale -> {
                    localeListener.onCreatedLocale(translationLocale);

                    return translationLocale;
                });
    }

    @Override
    @Transactional
    public Mono<TranslationLocaleEntity> update(TranslationLocaleDto localeDto) {
        return findById(localeDto.getId())
                .flatMap(locale -> localeListener.beforeUpdate(locale, localeDto).thenReturn(locale))
                .map(entity ->
                        entity
                                .setLanguage(localeDto.getLanguage())
                                .setRegion(localeDto.getRegion())
                                .setVariants(localeDto.getVariants())
                                .setIcon(localeDto.getIcon())
                )
                .map(translationLocale -> {
                    localeListener.onUpdatedLocale(translationLocale);

                    return translationLocale;
                });
    }

    @Override
    @Transactional
    public Mono<TranslationLocaleEntity> delete(String localeId) {
        return findById(localeId)
                .map(translationLocale -> {
                    localeListener.onDeletedLocale(translationLocale);

                    repository.delete(translationLocale);

                    return translationLocale;
                });
    }

}
