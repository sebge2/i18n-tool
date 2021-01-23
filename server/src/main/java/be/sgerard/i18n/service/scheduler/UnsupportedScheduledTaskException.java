package be.sgerard.i18n.service.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;

/**
 * Exception thrown when a {@link ScheduledTaskDefinition task} is not supported.
 *
 * @author Sebastien Gerard
 */
public class UnsupportedScheduledTaskException extends RuntimeException {

    public UnsupportedScheduledTaskException(String taskDefinitionId) {
        super(String.format("The task [%s] is not supported.", taskDefinitionId));
    }
}
