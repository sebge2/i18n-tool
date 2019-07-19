package be.sgerard.poc.githuboauth.model.event;

/**
 * @author Sebastien Gerard
 */
public abstract class ApplicationEvent extends org.springframework.context.ApplicationEvent {

    private final String type;

    public ApplicationEvent(Object source, String type) {
        super(source);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
