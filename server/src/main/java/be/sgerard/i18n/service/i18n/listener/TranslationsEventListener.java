package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author Sebastien Gerard
 */
@Component
public class TranslationsEventListener implements TranslationsListener{

    private final EventService eventService;

    public TranslationsEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Mono<Void> afterUpdate(BundleKeyTranslationEntity translation) {
        // TODO
//        eventService.broadcastEvent(
//                UPDATED_TRANSLATIONS,
//                new TranslationsUpdateEventDto(
//                        WorkspaceDto.builder(workspace).build(),
//                        currentUser,
//                        updatedEntries
//                )
//        );


        return Mono.empty();
    }
}
