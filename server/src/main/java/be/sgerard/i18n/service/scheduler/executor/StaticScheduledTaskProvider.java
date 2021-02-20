package be.sgerard.i18n.service.scheduler.executor;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;

import java.util.Collection;

/**
 * Provider of static scheduled tasks. Those tasks are always present during the lifetime of the application and should be recurring.
 *
 * @author Sebastien Gerard
 */
public interface StaticScheduledTaskProvider {

    /**
     * Returns the {@link ScheduledTaskDefinition tasks} to be scheduled.
     */
    Collection<ScheduledTaskDefinition> getTaskDefinitions();

}
