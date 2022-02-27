import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Repository } from '@i18n-core-translation';
import { RepositoryService } from '@i18n-core-translation';
import { ErrorMessagesDto, RepositoryPatchRequestDto } from '../../../../../../api';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { instanceOfErrorMessages, instanceOfHttpError } from '@i18n-core-shared';
import { RepositoryDetailsTranslationsGlobalConfigurationComponent } from './repository-details-translations-global-configuration/repository-details-translations-global-configuration.component';
import { RepositoryDetailsTranslationsBundleConfigurationComponent } from './repository-details-translations-bundle-configuration/repository-details-translations-bundle-configuration.component';

@Component({
  selector: 'app-repository-details-translations-configuration',
  templateUrl: './repository-details-translations-configuration.component.html',
  styleUrls: ['./repository-details-translations-configuration.component.css'],
})
export class RepositoryDetailsTranslationsConfigurationComponent implements OnInit, OnDestroy {
  readonly form: FormGroup;
  repository: Repository;

  saveInProgress: boolean = false;

  unknownError: any;

  errorMessages: ErrorMessagesDto;
  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _dialogRef: MatDialogRef<RepositoryDetailsTranslationsConfigurationComponent>,
    private _formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
    private _repositoryService: RepositoryService
  ) {
    this.form = _formBuilder.group({
      global: _formBuilder.group({}),
      javaProperties: _formBuilder.group({}),
      jsonIcu: _formBuilder.group({}),
    });
  }

  ngOnInit(): void {
    this._repositoryService
      .getRepository(this.data.repository.id)
      .pipe(takeUntil(this._destroyed$))
      .subscribe((repository) => {
        if (repository) {
          this.repository = repository;
        } else {
          this._dialogRef.close();
        }
      });
  }

  ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  get failed(): boolean {
    return this.unknownError || this.errorMessages;
  }

  get globalForm(): FormGroup {
    return <FormGroup>this.form.controls['global'];
  }

  get javaPropertiesForm(): FormGroup {
    return <FormGroup>this.form.controls['javaProperties'];
  }

  get jsonIcuForm(): FormGroup {
    return <FormGroup>this.form.controls['jsonIcu'];
  }

  onSave() {
    this.saveInProgress = true;

    this._repositoryService
      .updateRepository(this.data.repository.id, <RepositoryPatchRequestDto>{
        id: this.data.repository.id,
        type: this.data.repository.type,
        translationsConfiguration: {
          ignoredKeys: RepositoryDetailsTranslationsGlobalConfigurationComponent.getIgnoredProperties(this.globalForm),
          javaProperties: {
            ignoredPaths: RepositoryDetailsTranslationsBundleConfigurationComponent.getIgnoredPaths(
              this.javaPropertiesForm
            ),
          },
          jsonIcu: {
            ignoredPaths: RepositoryDetailsTranslationsBundleConfigurationComponent.getIgnoredPaths(this.jsonIcuForm),
          },
        },
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
