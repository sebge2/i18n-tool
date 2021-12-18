import { Component, Input, OnDestroy } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ErrorMessagesDto } from '../../../../../../api';
import { Subject } from 'rxjs';
import { instanceOfErrorMessages, instanceOfHttpError } from '@i18n-core-shared';
import { Repository } from '@i18n-core-translation';
import { takeUntil } from 'rxjs/operators';
import { RepositoryService } from '@i18n-core-translation';

@Component({
  selector: 'app-repository-add-wizard-step-initialization',
  templateUrl: './repository-add-wizard-step-initialization.component.html',
  styleUrls: ['./repository-add-wizard-step-initialization.component.css'],
})
export class RepositoryAddWizardStepInitializationComponent implements OnDestroy {
  @Input() form: FormGroup;

  initializationInProgress = false;
  unknownError: any;
  errorMessages: ErrorMessagesDto;

  private _originalRepository: Repository;
  private readonly _destroyed$ = new Subject<void>();

  constructor(private repositoryService: RepositoryService) {}

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  @Input()
  get originalRepository(): Repository {
    return this._originalRepository;
  }

  set originalRepository(value: Repository) {
    this._originalRepository = value;
    this.repository = null;
    this.unknownError = null;
    this.errorMessages = null;

    if (this._originalRepository) {
      this.initializationInProgress = true;

      this.repositoryService
        .initializeRepository(this.originalRepository.id)
        .pipe(takeUntil(this._destroyed$))
        .toPromise()
        .then((repository) => (this.repository = repository))
        .catch((error) => this._handleError(error))
        .finally(() => (this.initializationInProgress = false));
    }
  }

  get repository(): Repository {
    return this.form.controls['repository'].value;
  }

  set repository(repository: Repository) {
    this.form.controls['repository'].setValue(repository);
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
