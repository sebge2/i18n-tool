import { Component, Input, OnDestroy } from '@angular/core';
import { DictionaryService } from '../../../../service/dictionary.service';
import { DictionaryEntryCreationDto } from '../../../../../api';
import { FormGroup } from '@angular/forms';
import * as _ from 'lodash';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '@i18n-core-notification';

@Component({
  selector: 'app-dictionary-new-entry-action',
  templateUrl: './dictionary-new-entry-action.component.html',
})
export class DictionaryNewEntryActionComponent implements OnDestroy {
  @Input()
  public form: FormGroup;

  public saveInProgress: boolean = false;

  private readonly _destroyed$ = new Subject<void>();

  constructor(private _dictionaryService: DictionaryService, private _notificationService: NotificationService) {}

  public ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  public onSave(): void {
    this.saveInProgress = true;

    this._dictionaryService
      .createTranslation(this._createRequest())
      .pipe(takeUntil(this._destroyed$))
      .toPromise()
      .then(() => this._resetForm())
      .catch((error) => this._notificationService.displayErrorMessage('DICTIONARY.ERROR.CREATE', error))
      .finally(() => (this.saveInProgress = false));
  }

  private _createRequest(): DictionaryEntryCreationDto {
    const request: DictionaryEntryCreationDto = { translations: {} };

    _.forEach(
      _.keys(this.form.controls),
      (controlName) => (request.translations[controlName] = this.form.get(controlName).value)
    );

    return request;
  }

  private _resetForm(): void {
    _.forEach(_.keys(this.form.controls), (controlName) => this.form.get(controlName).setValue(null));

    this.form.markAsPristine();
  }
}
