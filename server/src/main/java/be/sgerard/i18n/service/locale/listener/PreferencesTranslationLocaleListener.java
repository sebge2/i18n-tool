package be.sgerard.i18n.service.locale.listener;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

/**
 * {@link TranslationLocaleListener Listener} applied when deleting a locale. User preferences will be impacted accordingly.
 *
 * @author Sebastien Gerard
 */
@Component
public class PreferencesTranslationLocaleListener implements TranslationLocaleListener {

    private final UserManager userManager;

    public PreferencesTranslationLocaleListener(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Mono<Void> beforeDelete(TranslationLocaleEntity locale) {
        return userManager
                .findAll()
                .flatMap(user -> {
                    final Optional<TranslationLocaleEntity> matchingLocale = user.getPreferences()
                            .getPreferredLocales().stream()
                            .filter(preferredLocale -> Objects.equals(locale.getId(), preferredLocale.getId()))
                            .findFirst();

                    if (matchingLocale.isPresent()) {
                        user.getPreferences().getPreferredLocales().remove(matchingLocale.get());

                        return userManager.update(user);
                    } else {
                        return Mono.empty();
                    }
                })
                .then();
    }
}
