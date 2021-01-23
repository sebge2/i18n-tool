import {Injectable} from '@angular/core';

import {
    ScheduledTaskDefinitionDto,
    ScheduledTaskExecutionDto,
    ScheduledTaskService as ApiScheduledTaskService
} from "../../api";
import {Observable, of} from "rxjs";
import {ScheduledTaskDefinition} from "../model/scheduled-task/scheduled-task-definition.model";
import {SynchronizedCollection} from "../../core/shared/utils/synchronized-collection";
import {EventService} from "../../core/event/service/event.service";
import {NotificationService} from "../../core/notification/service/notification.service";
import {Events} from "../../core/event/model/events.model";
import {catchError, distinctUntilChanged, map, tap} from "rxjs/operators";
import {ScheduledTaskExecution} from "../model/scheduled-task/scheduled-task-execution.model";
import * as _ from "lodash";

@Injectable({
    providedIn: 'root'
})
export class ScheduledTaskService {

    private readonly _synchronizedTaskDefinitions: SynchronizedCollection<ScheduledTaskDefinitionDto, ScheduledTaskDefinition>;
    private readonly _taskDefinitions$: Observable<ScheduledTaskDefinition[]>;

    private readonly _synchronizedTaskExecutions: SynchronizedCollection<ScheduledTaskExecutionDto, ScheduledTaskExecution>;
    private readonly _taskExecutions$: Observable<ScheduledTaskExecution[]>;

    constructor(private apiScheduledTaskService: ApiScheduledTaskService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._synchronizedTaskDefinitions = new SynchronizedCollection<ScheduledTaskDefinitionDto, ScheduledTaskDefinition>(
            () => apiScheduledTaskService.findDefinitions(),
            this.eventService.subscribeDto(Events.ADDED_SCHEDULED_TASK_DEFINITION),
            this.eventService.subscribeDto(Events.UPDATED_SCHEDULED_TASK_DEFINITION),
            this.eventService.subscribeDto(Events.DELETED_SCHEDULED_TASK_DEFINITION),
            this.eventService.reconnected(),
            dto => ScheduledTaskDefinition.fromDto(dto),
            ((first, second) => first.id === second.id)
        );

        this._taskDefinitions$ = this._synchronizedTaskDefinitions
            .collection
            .pipe(catchError((reason) => {
                console.error('Error while retrieving scheduled task definitions.', reason);
                this.notificationService.displayErrorMessage('ADMIN.SCHEDULED_TASKS.ERROR.GET_ALL_DEFINITIONS');
                return [];
            }));

        this._synchronizedTaskExecutions = new SynchronizedCollection<ScheduledTaskExecutionDto, ScheduledTaskExecution>(
            () => apiScheduledTaskService.findExecutions(),
            this.eventService.subscribeDto(Events.ADDED_SCHEDULED_TASK_EXECUTION),
            of(),
            this.eventService.subscribeDto(Events.DELETED_SCHEDULED_TASK_EXECUTION),
            this.eventService.reconnected(),
            dto => ScheduledTaskExecution.fromDto(dto),
            ((first, second) => first.id === second.id)
        );

        this._taskExecutions$ = this._synchronizedTaskExecutions
            .collection
            .pipe(catchError((reason) => {
                console.error('Error while retrieving scheduled task executions.', reason);
                this.notificationService.displayErrorMessage('ADMIN.SCHEDULED_TASKS.ERROR.GET_EXECUTIONS');
                return [];
            }));
    }

    public getTaskDefinitions(): Observable<ScheduledTaskDefinition[]> {
        return this._taskDefinitions$;
    }

    public getTaskDefinition(definitionId: string): Observable<ScheduledTaskDefinition | undefined> {
        return this.getTaskDefinitions()
            .pipe(
                map(definitions => _.find(definitions, definition => _.isEqual(definition.id, definitionId))),
                distinctUntilChanged()
            );
    }

    public getTaskExecutions(): Observable<ScheduledTaskExecution[]> {
        return this._taskExecutions$;
    }

    public enable(taskDefinition: ScheduledTaskDefinition): Observable<ScheduledTaskDefinition> {
        return this.apiScheduledTaskService
            .updateStatus(taskDefinition.id, 'ENABLE')
            .pipe(
                map(dto => ScheduledTaskDefinition.fromDto(dto)),
                tap(snapshot => this._synchronizedTaskDefinitions.update(snapshot)),
            );
    }

    public disable(taskDefinition: ScheduledTaskDefinition): Observable<ScheduledTaskDefinition> {
        return this.apiScheduledTaskService
            .updateStatus(taskDefinition.id, 'DISABLE')
            .pipe(
                map(dto => ScheduledTaskDefinition.fromDto(dto)),
                tap(snapshot => this._synchronizedTaskDefinitions.update(snapshot)),
            );
    }
}
