package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsUpdateEventDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.UPDATED_TRANSLATIONS;

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
    public Mono<Void> afterUpdate(BundleKeyEntity bundleKey, BundleKeyTranslationEntity translation) {
        return authenticationManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> userManager.findByIdOrDie(authenticatedUser.getUserId()))
                .flatMap(currentUser ->
                        eventService.broadcastEvent(
                                UPDATED_TRANSLATIONS,
                                TranslationsUpdateEventDto.builder()
                                        .userId(currentUser.getId())
                                        .userDisplayName(currentUser.getDisplayName())
                                        .translation(
                                                BundleKeyTranslationDto.builder(translation).build()
                                        )
                                        .build()
                        )
                );
    }
}
