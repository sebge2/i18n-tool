import {ScheduledTaskTrigger, ScheduledTaskTriggerType} from "./scheduled-task-trigger.model";

export class NonRecurringScheduledTaskTrigger extends ScheduledTaskTrigger {

    constructor(public startTime: Date) {
        super();
    }

    public type(): ScheduledTaskTriggerType {
        return ScheduledTaskTriggerType.NON_RECURRING;
    }

}
