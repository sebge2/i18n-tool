package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationDto;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationsUpdateEventDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.user.UserManager;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static be.sgerard.i18n.model.event.EventType.UPDATED_TRANSLATIONS;
import static java.util.stream.Collectors.toList;

/**
 * {@link TranslationsListener Translations listener} emitting an event every time translations are updated.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationsEventListener implements TranslationsListener {

    private final EventService eventService;
    private final AuthenticationUserManager authenticationManager;
    private final UserManager userManager;

    public TranslationsEventListener(EventService eventService,
                                     AuthenticationUserManager authenticationManager,
                                     UserManager userManager) {
        this.eventService = eventService;
        this.authenticationManager = authenticationManager;
        this.userManager = userManager;
    }

    @Override
    public Mono<Void> afterUpdate(List<Pair<BundleKeyTranslationEntity, BundleKeyEntity>> updates) {
        return authenticationManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> userManager.findByIdOrDie(authenticatedUser.getUserId()))
                .flatMap(currentUser ->
                        eventService.broadcastEvent(
                                UPDATED_TRANSLATIONS,
                                TranslationsUpdateEventDto.builder()
                                        .userId(currentUser.getId())
                                        .userDisplayName(currentUser.getDisplayName())
                                        .translations(
                                                updates.stream()
                                                        .map(Pair::getLeft)
                                                        .map(translation -> TranslationDto.builder(translation).build())
                                                        .collect(toList())
                                        )
                                        .build()
                        )
                );
    }
}
