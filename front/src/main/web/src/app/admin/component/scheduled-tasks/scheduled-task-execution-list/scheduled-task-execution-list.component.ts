import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subject } from 'rxjs';
import { ScheduledTaskService } from '../../../service/scheduled-task.service';
import { takeUntil } from 'rxjs/operators';
import * as _ from 'lodash';
import { ScheduledTaskExecution } from '../../../model/scheduled-task/scheduled-task-execution.model';
import { ScheduledTaskDefinition } from '../../../model/scheduled-task/scheduled-task-definition.model';

@Component({
  selector: 'app-scheduled-task-execution-list',
  templateUrl: './scheduled-task-execution-list.component.html',
  styleUrls: ['./scheduled-task-execution-list.component.scss'],
})
export class ScheduledTaskExecutionListComponent implements OnInit, OnDestroy {
  public readonly dataSource = new MatTableDataSource<ScheduledTaskExecution>();

  public actionInProgress: boolean = false;

  private readonly _destroyed$ = new Subject<void>();

  constructor(public scheduledTaskService: ScheduledTaskService) {}

  ngOnInit(): void {
    this.scheduledTaskService
      .getTaskExecutions()
      .pipe(takeUntil(this._destroyed$))
      .subscribe((definitions) => (this.dataSource.data = _.orderBy(definitions, ['startTime'], ['desc'])));
  }

  public ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  public getIconColor(execution: ScheduledTaskExecution): string {
    if (execution.successful) {
      return 'status-success';
    } else {
      return 'status-danger';
    }
  }

  public getIcon(execution: ScheduledTaskExecution): string {
    if (execution.successful) {
      return 'check';
    } else {
      return 'warning';
    }
  }

  public getTaskDefinition(execution: ScheduledTaskExecution): Observable<ScheduledTaskDefinition> {
    return this.scheduledTaskService.getTaskDefinition(execution.taskDefinitionId);
  }

  public hasExecutions(): boolean {
    return _.some(this.dataSource.data);
  }
}
