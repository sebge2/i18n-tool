import {ScheduledTaskExecutionDto} from "../../../api";
import {LocalizedString} from "../../../core/shared/model/localized-string.model";
import * as _ from "lodash";

export class ScheduledTaskExecution {
    public static fromDto(dto: ScheduledTaskExecutionDto): ScheduledTaskExecution {
        return new ScheduledTaskExecution(
            dto.id,
            dto.definitionId,
            dto.startTime,
            dto.endTime,
            ScheduledTaskExecutionStatus[dto.status],
            LocalizedString.fromDto(dto.shortDescription),
            LocalizedString.fromDto(dto.description),
            dto.durationInMs
        );
    }

    constructor(public id: string,
                public taskDefinitionId: string,
                public startTime: Date,
                public endTime: Date,
                public status: ScheduledTaskExecutionStatus,
                public shortDescription: LocalizedString,
                public description: LocalizedString,
                public durationInMs: number) {
    }

    public get successful(): boolean{
        return this.status === 'SUCCESSFUL';
    }

    public get descriptionOrFallback(): LocalizedString {
        return _.defaultTo(this.description, this.shortDescription);
    }
}

export enum ScheduledTaskExecutionStatus {

    SUCCESSFUL = 'SUCCESSFUL',

    FAILED = 'FAILED'
}
