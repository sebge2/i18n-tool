export abstract class ScheduledTaskTrigger {
  public abstract type(): ScheduledTaskTriggerType;
}

export enum ScheduledTaskTriggerType {
  RECURRING = 'RECURRING',

  NON_RECURRING = 'NON_RECURRING',
}
