package be.sgerard.i18n.model.event;

/**
 * @author Sebastien Gerard
 */
public enum EventType {

    EVENT_CONNECTED_USER_SESSION("connected-user-session"),

    EVENT_DISCONNECTED_USER_SESSION("disconnected-user-session"),

    EVENT_UPDATED_WORKSPACE("updated-workspace"),

    EVENT_DELETED_WORKSPACE("deleted-workspace"),

    EVENT_UPDATED_TRANSLATIONS("updated-translations"),

    EVENT_UPDATED_REPOSITORY("updated-repository"),

    /**
     * @see be.sgerard.i18n.model.security.user.UserDto
     */
    EVENT_UPDATED_USER("updated-user"),

    /**
     * @see be.sgerard.i18n.model.security.user.UserDto
     */
    EVENT_DELETED_USER("deleted-user"),

    /**
     * @see be.sgerard.i18n.model.security.user.UserDto
     */
    EVENT_UPDATED_CURRENT_USER("updated-current-user"),

    /**
     * @see be.sgerard.i18n.model.security.user.AuthenticatedUserDto
     */
    EVENT_UPDATED_AUTHENTICATED_USER("updated-authenticated-user"),

    /**
     * @see be.sgerard.i18n.model.security.user.AuthenticatedUserDto
     */
    EVENT_UPDATED_CURRENT_AUTHENTICATED_USER("updated-current-authenticated-user");

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
