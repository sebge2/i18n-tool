package be.sgerard.i18n.service.user.snapshot;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.user.persistence.UserPreferencesEntity;
import be.sgerard.i18n.model.user.snapshot.ExternalUserSnapshotDto;
import be.sgerard.i18n.model.user.snapshot.UserPreferencesSnapshotDto;
import be.sgerard.i18n.model.user.snapshot.UserSnapshotDto;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import be.sgerard.i18n.service.security.UserRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;

import static be.sgerard.i18n.service.ValidationException.monoSingleMessageValidationError;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Abstract implementation of a {@link UserSnapshotDtoMapper DTO mapper}.
 *
 * @author Sebastien Gerard
 */
public abstract class AbstractUserSnapshotDtoMapper<E extends UserEntity, D extends UserSnapshotDto> implements UserSnapshotDtoMapper<E, D> {

    /**
     * Validation message key specifying that a locale has not been found.
     */
    public static final String MISSING_LOCALE = "validation.snapshot.user.missing-locale";

    private final TranslationLocaleManager localeManager;

    protected AbstractUserSnapshotDtoMapper(TranslationLocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    /**
     * Fills the {@link UserEntity user} based on its {@link UserSnapshotDto DTO} representation.
     */
    @SuppressWarnings("unchecked")
    protected Mono<E> mapFromDto(UserEntity user, UserSnapshotDto dto) {
        return this
                .mapFromDto(user, dto.getPreferences())
                .map(preferences ->
                        (E) user
                                .setUsername(dto.getUsername())
                                .setId(dto.getId())
                                .setEmail(dto.getEmail())
                                .setRoles(mapFromDto(dto.getRoles()))
                                .setPreferences(preferences)
                                .setDisplayName(dto.getDisplayName())
                );
    }

    /**
     * Fills the builder with basic information coming from the user.
     */
    @SuppressWarnings("unchecked")
    protected Mono<D> mapToDto(UserSnapshotDto.Builder builder, UserEntity user) {
        return Mono.just(
                (D) builder
                        .id(user.getId())
                        .displayName(user.getDisplayName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .preferences(mapToDto(user.getPreferences()))
                        .roles(user.getRoles().stream().map(this::mapToDto).collect(toSet()))
                        .build()
        );
    }

    /**
     * Maps the authentication system from its DTO representation.
     */
    protected ExternalAuthSystem mapFromDto(ExternalUserSnapshotDto.ExternalAuthSystem externalAuthSystem) {
        // This way helps to figure out that there is a possible API change.
        switch (externalAuthSystem) {
            case OAUTH_GITHUB:
                return ExternalAuthSystem.OAUTH_GITHUB;
            case OAUTH_GOOGLE:
                return ExternalAuthSystem.OAUTH_GOOGLE;
            default:
                throw new UnsupportedOperationException("Unsupported external authentication system [" + externalAuthSystem + "].");
        }
    }

    /**
     * Maps user's preferences from their DTO representation.
     */
    protected Mono<UserPreferencesEntity> mapFromDto(UserEntity userEntity, UserPreferencesSnapshotDto preferences) {
        return Flux
                .fromIterable(preferences.getPreferredLocales())
                .flatMap(locale ->
                        localeManager
                                .findById(locale)
                                .switchIfEmpty(monoSingleMessageValidationError(() -> new ValidationMessage(MISSING_LOCALE, locale)))
                )
                .collectList()
                .map(locales ->
                        userEntity
                                .getPreferences()
                                .setToolLocale(preferences.getToolLocale().orElse(null))
                                .setPreferredLocales(
                                        locales.stream()
                                                .map(TranslationLocaleEntity::getId)
                                                .collect(toList())
                                )
                );
    }

    /**
     * Maps roles from their DTO representation.
     */
    protected Collection<UserRole> mapFromDto(Set<UserSnapshotDto.UserRole> roles) {
        return roles.stream().map(this::mapFromDto).collect(toSet());
    }

    /**
     * Maps the role from its DTO representation.
     */
    protected UserRole mapFromDto(UserSnapshotDto.UserRole role) {
        // This way helps to figure out that there is a possible API change.
        switch (role) {
            case ADMIN:
                return UserRole.ADMIN;
            case MEMBER_OF_ORGANIZATION:
                return UserRole.MEMBER_OF_ORGANIZATION;
            case MEMBER_OF_REPOSITORY:
                return UserRole.MEMBER_OF_REPOSITORY;
            default:
                throw new UnsupportedOperationException("Unsupported role [" + role + "].");
        }
    }

    /**
     * Maps the specified user's preferences to their DTO representation.
     */
    protected UserPreferencesSnapshotDto mapToDto(UserPreferencesEntity preferences) {
        return UserPreferencesSnapshotDto.builder()
                .preferredLocales(preferences.getPreferredLocales())
                .toolLocale(preferences.getToolLocale().orElse(null))
                .build();
    }

    /**
     * Maps the specified role to its DTO representation.
     */
    protected UserSnapshotDto.UserRole mapToDto(be.sgerard.i18n.service.security.UserRole role) {
        // This way helps to figure out that there is a possible API change.
        switch (role) {
            case ADMIN:
                return UserSnapshotDto.UserRole.ADMIN;
            case MEMBER_OF_ORGANIZATION:
                return UserSnapshotDto.UserRole.MEMBER_OF_ORGANIZATION;
            case MEMBER_OF_REPOSITORY:
                return UserSnapshotDto.UserRole.MEMBER_OF_REPOSITORY;
            default:
                throw new UnsupportedOperationException("Unsupported role [" + role + "].");
        }
    }

    /**
     * Maps the specified role to its DTO representation.
     */
    protected ExternalUserSnapshotDto.ExternalAuthSystem mapToDto(ExternalAuthSystem externalAuthSystem) {
        return ExternalUserSnapshotDto.ExternalAuthSystem.valueOf(externalAuthSystem.name());
    }

}
