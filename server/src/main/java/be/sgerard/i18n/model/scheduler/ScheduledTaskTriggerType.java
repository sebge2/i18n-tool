package be.sgerard.i18n.model.scheduler;

/**
 * Kind of trigger for a scheduled task.
 *
 * @author Sebastien Gerard
 */
public enum ScheduledTaskTriggerType {

    /**
     * Recurring execution of the task i.e., multiple execution of the task.
     */
    RECURRING,

    /**
     * Non-recurring execution of the task i.e., single execution of the task at a certain time.
     */
    NON_RECURRING;

    /**
     * Returns whether this task is recurring.
     */
    public boolean isRecurring() {
        return this == RECURRING;
    }
}
