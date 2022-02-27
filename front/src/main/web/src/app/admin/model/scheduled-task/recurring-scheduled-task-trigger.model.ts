import { ScheduledTaskTrigger, ScheduledTaskTriggerType } from './scheduled-task-trigger.model';

export class RecurringScheduledTaskTrigger extends ScheduledTaskTrigger {
  constructor(public cronExpression: string) {
    super();
  }

  type(): ScheduledTaskTriggerType {
    return ScheduledTaskTriggerType.RECURRING;
  }
}
