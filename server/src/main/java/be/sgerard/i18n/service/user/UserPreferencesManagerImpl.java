package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.user.dto.UserPreferencesDto;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.user.persistence.UserPreferencesEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.user.listener.UserPreferencesListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of the {@link UserPreferencesManager user preferences service}.
 *
 * @author Sebastien Gerard
 */
@Service
public class UserPreferencesManagerImpl implements UserPreferencesManager {

    /**
     * Validation message key specifying that a translation locale is missing.
     */
    public static final String MISSING_LOCALE = "validation.locale.missing";

    private final AuthenticationUserManager authenticationUserManager;
    private final UserManager userManager;
    private final UserPreferencesListener listener;
    private final TranslationLocaleManager translationLocaleManager;

    public UserPreferencesManagerImpl(AuthenticationUserManager authenticationUserManager,
                                      UserManager userManager,
                                      UserPreferencesListener listener,
                                      TranslationLocaleManager translationLocaleManager) {
        this.authenticationUserManager = authenticationUserManager;
        this.userManager = userManager;
        this.listener = listener;
        this.translationLocaleManager = translationLocaleManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserPreferencesEntity> get() throws ResourceNotFoundException {
        return getCurrentUserOrDie()
                .map(UserEntity::getPreferences);
    }

    @Override
    public Mono<UserPreferencesEntity> update(UserPreferencesDto preferences) throws ResourceNotFoundException {
        return getCurrentUserOrDie()
                .flatMap(user ->
                        mapLocales(preferences.getPreferredLocales())
                                .doOnNext(preferredLocales ->
                                        user.getPreferences()
                                                .setToolLocale(preferences.getToolLocale().orElse(null))
                                                .setPreferredLocales(preferredLocales)
                                )
                                .thenReturn(user)
                )
                .flatMap(userManager::update)
                .flatMap(user -> listener.afterUpdate(user).thenReturn(user.getPreferences()));

    }

    /**
     * Returns the current {@link UserEntity user}.
     */
    private Mono<UserEntity> getCurrentUserOrDie() {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .map(AuthenticatedUser::getUserId)
                .flatMap(userManager::findByIdOrDie);
    }

    /**
     * Maps all the translations locale ids to their entity.
     */
    private Mono<List<TranslationLocaleEntity>> mapLocales(Collection<String> translationLocales) {
        return Flux
                .fromIterable(translationLocales)
                .flatMap(id ->
                        translationLocaleManager
                                .findById(id)
                                .switchIfEmpty(Mono.error(
                                        new ValidationException(ValidationResult.singleMessage(new ValidationMessage(MISSING_LOCALE, id)))
                                ))
                )
                .collectList();
    }
}
