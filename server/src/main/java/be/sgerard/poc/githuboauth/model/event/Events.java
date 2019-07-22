package be.sgerard.poc.githuboauth.model.event;

/**
 * @author Sebastien Gerard
 */
public class Events {

    public static final String EVENT_CONNECTED_USER_SESSION = "connected-user-session";

    public static final String EVENT_DISCONNECTED_USER_SESSION = "disconnected-user-session";

    public static final String EVENT_UPDATED_WORKSPACE = "updated-workspace";

    public static final String EVENT_DELETED_WORKSPACE = "deleted-workspace";

    public static final String QUEUE_BROADCAST = "/topic/";

    public static final String QUEUE_USER = "/queue/";

    public static final String QUEUE_APP = "/app/";

}
