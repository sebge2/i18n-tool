import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Repository } from '../../model/repository/repository.model';
import { Workspace } from '../../model/workspace/workspace.model';
import { WorkspaceService } from '../../service/workspace.service';
import { Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import * as _ from 'lodash';
import { instanceOfErrorMessages, instanceOfHttpError } from '@i18n-core-shared';
import { ErrorMessagesDto } from '../../../../api';

@Component({
  selector: 'app-workspaces-start-review-dialog',
  templateUrl: './workspaces-start-review-dialog.component.html',
  styleUrls: ['./workspaces-start-review-dialog.component.css'],
})
export class WorkspacesStartReviewDialogComponent implements OnInit, OnDestroy {
  public readonly form: FormGroup;
  public publishInProgress: boolean = false;
  public workspaces: Workspace[] = [];

  public unknownError: any;
  public errorMessages: ErrorMessagesDto;

  private _destroyed$ = new Subject<void>();

  constructor(
    private dialogRef: MatDialogRef<WorkspacesStartReviewDialogComponent>,
    private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
    private _workspaceService: WorkspaceService
  ) {
    this.form = formBuilder.group({
      comment: ['', Validators.required],
      workspaces: [[], Validators.required],
    });
  }

  public ngOnInit(): void {
    this._workspaceService
      .getWorkspaces()
      .pipe(
        takeUntil(this._destroyed$),
        map((workspaces) => _.filter(workspaces, (workspace) => workspace.dirty))
      )
      .subscribe((availableWorkspaces) => {
        if (_.isEmpty(this.workspaces) && !_.isNil(this.data.repository)) {
          this.form.controls['workspaces'].setValue(
            _.filter(availableWorkspaces, (workspace) => _.isEqual(workspace.repositoryId, this.data.repository.id))
          );
        } else {
          this.form.controls['workspaces'].setValue(
            _.filter(availableWorkspaces, (availableWorkspace) =>
              _.some(this.workspacesToPublish, (workspaceToPublish) =>
                _.isEqual(workspaceToPublish.id, availableWorkspace.id)
              )
            )
          );
        }

        this.workspaces = availableWorkspaces;
      });
  }

  public ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  public get workspacesToPublish(): Workspace[] {
    return this.form.controls['workspaces'].value;
  }

  public get comment(): string {
    return this.form.controls['comment'].value;
  }

  public get failed(): boolean {
    return this.unknownError || this.errorMessages;
  }

  public onPublish() {
    this.publishInProgress = true;

    this._workspaceService
      .publishAll(
        this.workspacesToPublish.map((workspace) => workspace.id),
        this.comment
      )
      .toPromise()
      .then(() => this.dialogRef.close())
      .catch((error) => this.handleError(error))
      .finally(() => (this.publishInProgress = false));
  }

  public onGoBack() {
    this.errorMessages = null;
    this.unknownError = null;
  }

  private handleError(cause: any) {
    if (instanceOfHttpError(cause)) {
      return this.handleError(cause.error);
    } else if (instanceOfErrorMessages(cause)) {
      this.errorMessages = <ErrorMessagesDto>cause;
    } else {
      this.unknownError = cause;
    }
  }
}
