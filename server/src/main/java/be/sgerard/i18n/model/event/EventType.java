package be.sgerard.i18n.model.event;

import be.sgerard.i18n.model.i18n.dto.TranslationsUpdateEventDto;
import be.sgerard.i18n.model.locale.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskExecutionDto;
import be.sgerard.i18n.model.security.auth.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.security.session.dto.UserLiveSessionDto;
import be.sgerard.i18n.model.snapshot.dto.SnapshotDto;
import be.sgerard.i18n.model.user.dto.UserDto;
import be.sgerard.i18n.model.user.dto.UserPreferencesDto;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;

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
     * @see RepositoryDto
     */
    ADDED_REPOSITORY,

    /**
     * @see RepositoryDto
     */
    UPDATED_REPOSITORY,

    /**
     * @see RepositoryDto
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
     * @see AuthenticatedUserDto
     */
    DELETED_CURRENT_AUTHENTICATED_USER,

    /**
     * @see UserPreferencesDto
     */
    UPDATED_USER_PREFERENCES,

    /**
     * @see SnapshotDto
     */
    ADDED_SNAPSHOT,

    /**
     * @see SnapshotDto
     */
    DELETED_SNAPSHOT,

    /**
     * @see ScheduledTaskDefinitionDto
     */
    ADDED_SCHEDULED_TASK_DEFINITION,

    /**
     * @see ScheduledTaskDefinitionDto
     */
    UPDATED_SCHEDULED_TASK_DEFINITION,

    /**
     * @see ScheduledTaskDefinitionDto
     */
    DELETED_SCHEDULED_TASK_DEFINITION,

    /**
     * @see ScheduledTaskExecutionDto
     */
    ADDED_SCHEDULED_TASK_EXECUTION,

    /**
     * @see ScheduledTaskExecutionDto
     */
    DELETED_SCHEDULED_TASK_EXECUTION,
}
