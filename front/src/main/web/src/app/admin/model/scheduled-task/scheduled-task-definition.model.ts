import {
    NonRecurringScheduledTaskTriggerDto,
    RecurringScheduledTaskTriggerDto,
    ScheduledTaskDefinitionDto,
    ScheduledTaskTriggerDto
} from "../../../api";
import {LocalizedString} from "../../../core/shared/model/localized-string.model";
import {ScheduledTaskTrigger} from "./scheduled-task-trigger.model";
import {NonRecurringScheduledTaskTrigger} from "./non-recurring-scheduled-task-trigger.model";
import {RecurringScheduledTaskTrigger} from "./recurring-scheduled-task-trigger.model";

export class ScheduledTaskDefinition {

    public static fromDto(dto: ScheduledTaskDefinitionDto): ScheduledTaskDefinition {
        return new ScheduledTaskDefinition(
            dto.id,
            LocalizedString.fromDto(dto.name),
            LocalizedString.fromDto(dto.description),
            dto.enabled,
            ScheduledTaskDefinition.fromTriggerDto(dto.trigger),
            dto.lastExecutionTime,
            dto.nextExecutionTime
        );
    }

    public static fromTriggerDto(dto: ScheduledTaskTriggerDto): ScheduledTaskTrigger {
        switch (dto.type) {
            case "NON_RECURRING":
                return new NonRecurringScheduledTaskTrigger((<NonRecurringScheduledTaskTriggerDto>dto).startTime);
            case "RECURRING":
                return new RecurringScheduledTaskTrigger((<RecurringScheduledTaskTriggerDto>dto).cronExpression);
        }
    }

    constructor(public id: string,
                public name: LocalizedString,
                public description: LocalizedString,
                public enabled: boolean,
                public trigger: ScheduledTaskTrigger,
                public lastExecutionTime?: Date,
                public nextExecutionTime?: Date) {
    }
}
