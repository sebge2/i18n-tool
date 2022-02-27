package be.sgerard.i18n.service.dictionary.listener;

import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryDto;
import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link DictionaryListener Dictionary listener} emitting events notifying updates about entries.
 *
 * @author Sebastien Gerard
 */
@Component
public class DictionaryEventListener implements DictionaryListener {

    private final EventService eventService;

    public DictionaryEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Mono<Void> afterPersist(DictionaryEntryEntity entry) {
        return eventService.sendEventToUsers(UserRole.MEMBER_OF_ORGANIZATION, EventType.ADDED_DICTIONARY_ENTRY, DictionaryEntryDto.builder(entry).build());
    }

    @Override
    public Mono<Void> afterUpdate(DictionaryEntryEntity entry) {
        return eventService
                .sendEventToUsers(UserRole.MEMBER_OF_ORGANIZATION, EventType.UPDATED_DICTIONARY_ENTRY, DictionaryEntryDto.builder(entry).build())
                .then();
    }

    @Override
    public Mono<Void> afterDelete(DictionaryEntryEntity entry) {
        return eventService
                .sendEventToUsers(UserRole.MEMBER_OF_ORGANIZATION, EventType.DELETED_DICTIONARY_ENTRY, DictionaryEntryDto.builder(entry).build())
                .then();
    }
}
