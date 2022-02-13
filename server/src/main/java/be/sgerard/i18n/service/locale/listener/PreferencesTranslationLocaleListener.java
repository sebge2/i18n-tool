package be.sgerard.i18n.service.locale.listener;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
                .filter(user -> user.getPreferences().getPreferredLocales().contains(locale.getId()))
                .flatMap(user -> {
                    user.getPreferences().getPreferredLocales().remove(locale.getId());

                    return userManager.update(user);
                })
                .then();
    }
}
