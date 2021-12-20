import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorMessagesDto, GitHubRepositoryPatchRequestDto } from '../../../../../../api';
import { Subject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Repository } from '@i18n-core-translation';
import { RepositoryService } from '@i18n-core-translation';
import { filter, takeUntil } from 'rxjs/operators';
import { instanceOfErrorMessages, instanceOfHttpError } from '@i18n-core-shared';

@Component({
  selector: 'app-repository-github-access-key-dialog',
  templateUrl: './repository-github-access-key-dialog.component.html',
  styleUrls: ['./repository-github-access-key-dialog.component.css'],
})
export class RepositoryGithubAccessKeyDialogComponent implements OnInit, OnDestroy {
  readonly form: FormGroup;

  saveInProgress: boolean = false;
  deleteInProgress: boolean = false;

  unknownError: any;

  errorMessages: ErrorMessagesDto;
  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _dialogRef: MatDialogRef<RepositoryGithubAccessKeyDialogComponent>,
    private _formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
    private _repositoryService: RepositoryService
  ) {
    this.form = _formBuilder.group({
      accessKey: ['', Validators.required],
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
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  get failed(): boolean {
    return this.unknownError || this.errorMessages;
  }

  onSave() {
    this.saveInProgress = true;

    this._repositoryService
      .updateRepository(this.data.repository.id, <GitHubRepositoryPatchRequestDto>{
        id: this.data.repository.id,
        type: 'GITHUB',
        accessKey: this.form.controls['accessKey'].value,
      })
      .toPromise()
      .then(() => this._dialogRef.close())
      .catch((error) => this._handleError(error))
      .finally(() => (this.saveInProgress = false));
  }

  onDelete() {
    this.deleteInProgress = true;

    this._repositoryService
      .updateRepository(this.data.repository.id, <GitHubRepositoryPatchRequestDto>{
        id: this.data.repository.id,
        type: 'GITHUB',
        accessKey: '',
      })
      .toPromise()
      .then(() => this._dialogRef.close())
      .catch((error) => this._handleError(error))
      .finally(() => (this.deleteInProgress = false));
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
