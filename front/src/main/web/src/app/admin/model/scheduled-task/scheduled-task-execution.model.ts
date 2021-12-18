import { ScheduledTaskExecutionDto } from '../../../api';
import { LocalizedString } from '@i18n-core-translation';
import * as _ from 'lodash';

export class ScheduledTaskExecution {
  static fromDto(dto: ScheduledTaskExecutionDto): ScheduledTaskExecution {
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

  constructor(
    public id: string,
    public taskDefinitionId: string,
    public startTime: Date,
    public endTime: Date,
    public status: ScheduledTaskExecutionStatus,
    public shortDescription: LocalizedString,
    public description: LocalizedString,
    public durationInMs: number
  ) {}

  get successful(): boolean {
    return this.status === 'SUCCESSFUL';
  }

  get descriptionOrFallback(): LocalizedString {
    return _.defaultTo(this.description, this.shortDescription);
  }
}

export enum ScheduledTaskExecutionStatus {
  SUCCESSFUL = 'SUCCESSFUL',

  FAILED = 'FAILED',
}
