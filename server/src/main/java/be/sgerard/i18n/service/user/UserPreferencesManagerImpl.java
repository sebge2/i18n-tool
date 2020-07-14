package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserPreferencesEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.i18n.TranslationLocaleManager;
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
    public static final String MISSING_VALIDATION_LOCALE = "validation.locale.missing";

    private final UserManager userManager;
    private final UserPreferencesListener listener;
    private final TranslationLocaleManager translationLocaleManager;

    public UserPreferencesManagerImpl(UserManager userManager,
                                      UserPreferencesListener listener,
                                      TranslationLocaleManager translationLocaleManager) {
        this.userManager = userManager;
        this.listener = listener;
        this.translationLocaleManager = translationLocaleManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserPreferencesEntity> find(String userId) throws ResourceNotFoundException {
        return findUserOrDie(userId)
                .map(UserEntity::getPreferences);
    }

    @Override
    public Mono<UserPreferencesEntity> update(String userId, UserPreferencesDto preferences) throws ResourceNotFoundException {
        return userManager
                .findByIdOrDie(userId)
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
                .flatMap(user ->
                        listener
                                .onUpdate(user)
                                .thenReturn(user.getPreferences())
                );

    }

    /**
     * Returns the {@link UserEntity user} having the specified id.
     */
    private Mono<UserEntity> findUserOrDie(String userId) {
        return userManager.findByIdOrDie(userId);
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
                                        new ValidationException(ValidationResult.builder().messages(new ValidationMessage(MISSING_VALIDATION_LOCALE, id)).build())
                                ))
                )
                .collectList();
    }
}
