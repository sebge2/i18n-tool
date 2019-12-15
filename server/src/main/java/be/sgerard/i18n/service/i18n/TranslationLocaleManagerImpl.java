package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.i18n.TranslationLocaleRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;

/**
 * Implementation of the {@link TranslationLocaleManager translation locale manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class TranslationLocaleManagerImpl implements TranslationLocaleManager {

    private final TranslationLocaleRepository repository;
    private final EventService eventService;

    public TranslationLocaleManagerImpl(TranslationLocaleRepository repository, EventService eventService) {
        this.repository = repository;
        this.eventService = eventService;
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationLocaleEntity findById(String id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.translationLocaleNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TranslationLocaleEntity> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public TranslationLocaleEntity create(TranslationLocaleCreationDto locale) {
        final TranslationLocaleEntity translationLocale = repository.save(
                new TranslationLocaleEntity(
                        locale.getLanguage(),
                        locale.getRegion(),
                        locale.getVariants(),
                        locale.getIcon()
                )
        );

        validateUnique(translationLocale);

        eventService.broadcastEvent(EventType.ADDED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());

        return translationLocale;
    }

    @Override
    @Transactional
    public TranslationLocaleEntity update(TranslationLocaleDto locale) {
        final TranslationLocaleEntity translationLocale = repository.findById(locale.getId())
                .map(entity ->
                        entity
                                .setLanguage(locale.getLanguage())
                                .setRegion(locale.getRegion())
                                .setVariants(locale.getVariants())
                                .setIcon(locale.getIcon())
                )
                .orElseThrow(() -> ResourceNotFoundException.translationLocaleNotFoundException(locale.getId()));

        validateUnique(translationLocale);

        eventService.broadcastEvent(EventType.UPDATED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());

        return translationLocale;
    }

    @Override
    @Transactional
    public void delete(String localeId) {
        repository.findById(localeId)
                .ifPresent(translationLocale -> {
                    eventService.broadcastEvent(EventType.DELETED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());

                    repository.delete(translationLocale);
                });
    }

    /**
     * Validates that the specified locale is unique.
     */
    private void validateUnique(TranslationLocaleEntity translationLocale) {
        findAll().stream()
                .filter(existingTranslationLocale -> !Objects.equals(existingTranslationLocale.getId(), translationLocale.getId()))
                .forEach(existingTranslationLocale -> {
                    if (existingTranslationLocale.matchLocale(translationLocale)) {
                        ValidationException.throwIfFailed(
                                ValidationResult.builder().messages(new ValidationMessage("DUPLICATE_LOCALE")).build()
                        );
                    }
                });
    }
}
