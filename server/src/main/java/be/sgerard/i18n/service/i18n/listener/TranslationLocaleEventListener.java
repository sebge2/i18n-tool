package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;

/**
 * {@link TranslationLocaleListener Locale listener} emitting events.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationLocaleEventListener implements TranslationLocaleListener {

    private final EventService eventService;

    public TranslationLocaleEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void onCreatedLocale(TranslationLocaleEntity translationLocale) {
        eventService.broadcastEvent(EventType.ADDED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());
    }

    @Override
    public void onUpdatedLocale(TranslationLocaleEntity translationLocale) {
        eventService.broadcastEvent(EventType.UPDATED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());
    }

    @Override
    public void onDeletedLocale(TranslationLocaleEntity translationLocale) {
        eventService.broadcastEvent(EventType.DELETED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());
    }
}
