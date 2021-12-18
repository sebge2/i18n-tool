import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorMessagesDto, GitHubRepositoryPatchRequestDto } from '../../../../../../api';
import { Subject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Repository } from '@i18n-core-translation';
import { RepositoryService } from '@i18n-core-translation';
import {instanceOfErrorMessages, instanceOfHttpError, TranslationKey} from '@i18n-core-shared';
import { filter, takeUntil } from 'rxjs/operators';
import {NotificationService} from "@i18n-core-notification";

@Component({
  selector: 'app-repository-github-web-hook-dialog',
  templateUrl: './repository-github-web-hook-dialog.component.html',
  styleUrls: ['./repository-github-web-hook-dialog.component.css'],
})
export class RepositoryGithubWebHookDialogComponent implements OnInit, OnDestroy {
  readonly form: FormGroup;

  saveInProgress: boolean = false;
  deleteInProgress: boolean = false;

  unknownError: any;

  errorMessages: ErrorMessagesDto;
  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _dialogRef: MatDialogRef<RepositoryGithubWebHookDialogComponent>,
    private _formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
    private _repositoryService: RepositoryService,
    private _notificationService: NotificationService
  ) {
    this.form = _formBuilder.group({
      secret: ['', Validators.required],
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

  onGeneratedSecret(generatedSecret: string) {
    this.form.controls['secret'].setValue(generatedSecret);
    this.form.controls['secret'].markAsDirty();

    this._notificationService.displayInfoMessage(new TranslationKey('ADMIN.REPOSITORIES.CONFIG_CARD.GITHUB_WEB_HOOK_POPUP.SECRET_COPIED'));
  }

  onSave() {
    this.saveInProgress = true;

    this._repositoryService
      .updateRepository(this.data.repository.id, <GitHubRepositoryPatchRequestDto>{
        id: this.data.repository.id,
        type: 'GITHUB',
        webHookSecret: this.form.controls['secret'].value,
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
        webHookSecret: '',
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
