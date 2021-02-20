import {ScheduledTaskTrigger, ScheduledTaskTriggerType} from "./scheduled-task-trigger.model";

export class RecurringScheduledTaskTrigger extends ScheduledTaskTrigger {

    constructor(public cronExpression: string) {
        super();
    }

    public type(): ScheduledTaskTriggerType {
        return ScheduledTaskTriggerType.RECURRING;
    }
}
