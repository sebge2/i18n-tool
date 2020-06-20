package be.sgerard.i18n.model.event;

import be.sgerard.i18n.model.repository.dto.RepositorySummaryDto;
import be.sgerard.i18n.model.security.session.dto.UserLiveSessionDto;
import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.model.workspace.WorkspaceDto;

/**
 * @author Sebastien Gerard
 */
public enum EventType {

    /**
     * @see UserLiveSessionDto
     */
    CONNECTED_USER_SESSION("connected-user-session"),

    /**
     * @see be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto
     */
    ADDED_TRANSLATION_LOCALE("added-translation-locale"),

    /**
     * @see be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto
     */
    UPDATED_TRANSLATION_LOCALE("updated-translation-locale"),

    /**
     * @see be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto
     */
    DELETED_TRANSLATION_LOCALE("deleted-translation-locale"),

    /**
     * @see UserLiveSessionDto
     */
    DISCONNECTED_USER_SESSION("disconnected-user-session"),

    /**
     * @see WorkspaceDto
     */
    ADDED_WORKSPACE("added-workspace"),

    /**
     * @see WorkspaceDto
     */
    UPDATED_WORKSPACE("updated-workspace"),

    /**
     * @see WorkspaceDto
     */
    DELETED_WORKSPACE("deleted-workspace"),

    /**
     * @see be.sgerard.i18n.model.i18n.dto.TranslationsUpdateEventDto
     */
    UPDATED_TRANSLATIONS("updated-translations"),

    /**
     * @see RepositorySummaryDto
     */
    ADDED_REPOSITORY("added-repository"),

    /**
     * @see RepositorySummaryDto
     */
    UPDATED_REPOSITORY("updated-repository"),

    /**
     * @see RepositorySummaryDto
     */
    DELETED_REPOSITORY("deleted-repository"),

    /**
     * @see UserDto
     */
    UPDATED_USER("updated-user"),

    /**
     * @see UserDto
     */
    DELETED_USER("deleted-user"),

    /**
     * @see UserDto
     */
    UPDATED_CURRENT_USER("updated-current-user"),

    /**
     * @see AuthenticatedUserDto
     */
    UPDATED_AUTHENTICATED_USER("updated-authenticated-user"),

    /**
     * @see AuthenticatedUserDto
     */
    UPDATED_CURRENT_AUTHENTICATED_USER("updated-current-authenticated-user"),

    /**
     * @see UserPreferencesDto
     */
    UPDATED_USER_PREFERENCES("updated-current-user-preferences");

    public static final String QUEUE_BROADCAST = "/topic/";

    public static final String QUEUE_USER = "/queue/";

    public static final String QUEUE_APP = "/app/";

    private final String name;

    EventType(String name) {
        this.name = name;
    }

    public String toBroadcastQueue() {
        return QUEUE_BROADCAST + "/" + name;
    }

    public String toUserQueue() {
        return QUEUE_USER + "/" + name;
    }
}
