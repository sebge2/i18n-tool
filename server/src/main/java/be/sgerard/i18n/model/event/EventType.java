package be.sgerard.i18n.model.event;

import be.sgerard.i18n.model.repository.dto.RepositorySummaryDto;
import be.sgerard.i18n.model.security.session.dto.UserLiveSessionDto;
import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsUpdateEventDto;

/**
 * All possible kind of events.
 *
 * @author Sebastien Gerard
 */
public enum EventType {

    /**
     * @see UserLiveSessionDto
     */
    CONNECTED_USER_SESSION,

    /**
     * @see TranslationLocaleDto
     */
    ADDED_TRANSLATION_LOCALE,

    /**
     * @see TranslationLocaleDto
     */
    UPDATED_TRANSLATION_LOCALE,

    /**
     * @see TranslationLocaleDto
     */
    DELETED_TRANSLATION_LOCALE,

    /**
     * @see UserLiveSessionDto
     */
    DISCONNECTED_USER_SESSION,

    /**
     * @see WorkspaceDto
     */
    ADDED_WORKSPACE,

    /**
     * @see WorkspaceDto
     */
    UPDATED_WORKSPACE,

    /**
     * @see WorkspaceDto
     */
    DELETED_WORKSPACE,

    /**
     * @see TranslationsUpdateEventDto
     */
    UPDATED_TRANSLATIONS,

    /**
     * @see RepositorySummaryDto
     */
    ADDED_REPOSITORY,

    /**
     * @see RepositorySummaryDto
     */
    UPDATED_REPOSITORY,

    /**
     * @see RepositorySummaryDto
     */
    DELETED_REPOSITORY,

    /**
     * @see UserDto
     */
    ADDED_USER,

    /**
     * @see UserDto
     */
    UPDATED_USER,

    /**
     * @see UserDto
     */
    DELETED_USER,

    /**
     * @see UserDto
     */
    UPDATED_CURRENT_USER,

    /**
     * @see AuthenticatedUserDto
     */
    UPDATED_AUTHENTICATED_USER,

    /**
     * @see AuthenticatedUserDto
     */
    DELETED_AUTHENTICATED_USER,

    /**
     * @see AuthenticatedUserDto
     */
    UPDATED_CURRENT_AUTHENTICATED_USER,

    /**
     * @see UserPreferencesDto
     */
    UPDATED_USER_PREFERENCES;
}
