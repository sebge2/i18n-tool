package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
    public Mono<Void> onCreatedLocale(TranslationLocaleEntity translationLocale) {
        return eventService.broadcastEvent(EventType.ADDED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());
    }

    @Override
    public Mono<Void> onUpdatedLocale(TranslationLocaleEntity translationLocale) {
        return eventService.broadcastEvent(EventType.UPDATED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());
    }

    @Override
    public Mono<Void> onDeletedLocale(TranslationLocaleEntity translationLocale) {
        return eventService.broadcastEvent(EventType.DELETED_TRANSLATION_LOCALE, TranslationLocaleDto.builder(translationLocale).build());
    }
}