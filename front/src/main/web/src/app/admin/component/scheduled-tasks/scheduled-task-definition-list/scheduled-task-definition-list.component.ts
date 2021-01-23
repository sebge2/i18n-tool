import {Component, OnDestroy, OnInit} from '@angular/core';
import {ScheduledTaskService} from "../../../service/scheduled-task.service";
import {MatTableDataSource} from "@angular/material/table";
import {ScheduledTaskDefinition} from "../../../model/scheduled-task/scheduled-task-definition.model";
import {takeUntil} from "rxjs/operators";
import * as _ from "lodash";
import {Subject} from "rxjs";
import {ScheduledTaskTriggerType} from "../../../model/scheduled-task/scheduled-task-trigger.model";
import {PlayButtonState} from "../../../../core/shared/component/play-button/play-button.component";
import {NotificationService} from "../../../../core/notification/service/notification.service";

@Component({
    selector: 'app-scheduled-task-definition-list',
    templateUrl: './scheduled-task-definition-list.component.html',
    styleUrls: ['./scheduled-task-definition-list.component.css']
})
export class ScheduledTaskDefinitionListComponent implements OnInit, OnDestroy {

    public readonly dataSource = new MatTableDataSource<ScheduledTaskDefinition>();

    public actionInProgress: boolean = false;

    private readonly _destroyed$ = new Subject<void>();

    constructor(public scheduledTaskService: ScheduledTaskService,
                private notificationService: NotificationService) {
    }

    ngOnInit(): void {
        this.scheduledTaskService
            .getTaskDefinitions()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(definitions => this.dataSource.data = _.orderBy(definitions, ['id'], ['desc']));
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public getIconColor(definition: ScheduledTaskDefinition): string {
        if (definition.enabled) {
            return 'status-success';
        } else {
            return 'status-danger';
        }
    }

    public getIcon(definition: ScheduledTaskDefinition): string {
        switch (definition.trigger.type()) {
            case ScheduledTaskTriggerType.NON_RECURRING:
                return 'calendar_today';
            case ScheduledTaskTriggerType.RECURRING:
                return 'loop';
            default:
                return '';
        }
    }

    public getPlayState(definition: ScheduledTaskDefinition): PlayButtonState {
        return definition.enabled
            ? PlayButtonState.STARTED
            : PlayButtonState.STOPPED;
    }

    public onChangeStatus(definition: ScheduledTaskDefinition) {
        const obs = definition.enabled
            ? this.scheduledTaskService.disable(definition)
            : this.scheduledTaskService.enable(definition);

        this.actionInProgress = true;

        obs
            .toPromise()
            .catch(error => this.notificationService.displayErrorMessage('ADMIN.SCHEDULED_TASKS.ERROR.SCHEDULE_UNSCHEDULE', error))
            .finally(() => this.actionInProgress = false);
    }
}
