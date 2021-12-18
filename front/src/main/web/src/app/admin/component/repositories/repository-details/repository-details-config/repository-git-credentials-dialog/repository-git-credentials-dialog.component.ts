import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ErrorMessagesDto, GitRepositoryPatchRequestDto } from '../../../../../../api';
import { Subject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Repository } from '@i18n-core-translation';
import { RepositoryService } from '@i18n-core-translation';
import { filter, takeUntil } from 'rxjs/operators';
import { instanceOfErrorMessages, instanceOfHttpError } from '@i18n-core-shared';
import { getStringValue } from '@i18n-core-shared';

@Component({
  selector: 'app-repository-git-credentials-dialog',
  templateUrl: './repository-git-credentials-dialog.component.html',
  styleUrls: ['./repository-git-credentials-dialog.component.css'],
})
export class RepositoryGitCredentialsDialogComponent implements OnInit, OnDestroy {
  readonly form: FormGroup;

  saveInProgress: boolean = false;
  deleteInProgress: boolean = false;

  unknownError: any;

  errorMessages: ErrorMessagesDto;
  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _dialogRef: MatDialogRef<RepositoryGitCredentialsDialogComponent>,
    private _formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
    private _repositoryService: RepositoryService
  ) {
    this.form = _formBuilder.group({
      username: [],
      password: [],
    });
  }

  ngOnInit(): void {
    this._repositoryService
      .getRepository(this.data.repository.id)
      .pipe(
        takeUntil(this._destroyed$),
        filter((repository) => !repository)
      )
      .subscribe(() => this._dialogRef.close());
  }

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  get failed(): boolean {
    return this.unknownError || this.errorMessages;
  }

  onSave() {
    this.saveInProgress = true;

    this._repositoryService
      .updateRepository(this.data.repository.id, <GitRepositoryPatchRequestDto>{
        id: this.data.repository.id,
        type: 'GIT',
        username: getStringValue(this.form.controls['username']),
        password: getStringValue(this.form.controls['password']),
      })
      .toPromise()
      .then(() => this._dialogRef.close())
      .catch((error) => this._handleError(error))
      .finally(() => (this.saveInProgress = false));
  }

  onGoBack() {
    this.errorMessages = null;
    this.unknownError = null;
  }

  private _handleError(cause: any) {
    if (instanceOfHttpError(cause)) {
      return this._handleError(cause.error);
    } else if (instanceOfErrorMessages(cause)) {
      this.errorMessages = <ErrorMessagesDto>cause;
    } else {
      this.unknownError = cause;
    }
  }
}
