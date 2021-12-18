import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TranslationLocale } from '@i18n-core-translation';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TranslationLocaleService } from '@i18n-core-translation';
import { NotificationService } from '@i18n-core-notification';
import * as _ from 'lodash';
import { TranslationLocaleCreationDto } from '../../../../api';
import { Locale } from '@i18n-core-translation';
import { startWith } from 'rxjs/operators';
import { getStringValue } from '@i18n-core-shared';

@Component({
  selector: 'app-locale-view-card',
  templateUrl: './locale-view-card.component.html',
  styleUrls: ['./locale-view-card.component.css'],
})
export class LocaleViewCardComponent implements OnInit {
  @Output() save = new EventEmitter<TranslationLocale>();
  @Output() delete = new EventEmitter<TranslationLocale>();

  readonly form: FormGroup;

  cancelInProgress: boolean = false;
  deleteInProgress: boolean = false;
  saveInProgress: boolean = false;

  _title: string;
  _locale: TranslationLocale;

  constructor(
    private _formBuilder: FormBuilder,
    private _translationLocaleService: TranslationLocaleService,
    private _notificationService: NotificationService
  ) {
    this.form = this._formBuilder.group({
      displayName: this._formBuilder.control('', []),
      language: this._formBuilder.control('', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]),
      region: this._formBuilder.control('', [Validators.minLength(2), Validators.maxLength(2)]),
      variants: this._formBuilder.control('', []),
      icon: this._formBuilder.control('', [Validators.required]),
    });
  }

  ngOnInit() {
    this.form.valueChanges.pipe(startWith(<Object>null)).subscribe((_) => this._resetTitle());
  }

  @Input()
  get locale() {
    return this._locale;
  }

  set locale(value: TranslationLocale) {
    this._locale = value;

    this._resetForm();
  }

  get title(): string {
    return this._title;
  }

  get displayName(): string {
    return getStringValue(this.form.controls['displayName']);
  }

  get language(): string {
    let language = getStringValue(this.form.controls['language']);

    return _.isEmpty(language) ? null : language.toLowerCase();
  }

  get region(): string {
    let region = getStringValue(this.form.controls['region']);

    return _.isEmpty(region) ? null : region.toUpperCase();
  }

  get variants(): string[] {
    let variants = getStringValue(this.form.controls['variants']);

    return _.isEmpty(variants) ? [] : variants.split(' ');
  }

  get icon(): string {
    return getStringValue(this.form.controls['icon']);
  }

  get iconClass(): string {
    return `flag-icon ${this.form.controls['icon'].value}`;
  }

  get actionInProgress(): boolean {
    return this.cancelInProgress || this.saveInProgress || this.deleteInProgress;
  }

  onCancel() {
    this.cancelInProgress = true;
    this._resetForm();
    this.cancelInProgress = false;
  }

  onSave() {
    this.saveInProgress = true;

    if (this.locale.id) {
      this._translationLocaleService
        .updateLocale(this._toUpdatedLocale())
        .toPromise()
        .then((translationLocale) => (this.locale = translationLocale))
        .then((translationLocale) => this.save.emit(translationLocale))
        .catch((error) => this._notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.UPDATE', error))
        .finally(() => (this.saveInProgress = false));
    } else {
      this._translationLocaleService
        .createLocale(this._toNewLocale())
        .toPromise()
        .then((translationLocale) => (this.locale = translationLocale))
        .then((translationLocale) => this.save.emit(translationLocale))
        .catch((error) => this._notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.SAVE', error))
        .finally(() => (this.saveInProgress = false));
    }
  }

  onDelete() {
    if (this.isExistingLocale()) {
      this.deleteInProgress = true;
      this._translationLocaleService
        .deleteLocale(this.locale)
        .toPromise()
        .then((translationLocale) => (this.locale = translationLocale))
        .then((translationLocale) => this.save.emit(translationLocale))
        .catch((error) => this._notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.DELETE', error))
        .finally(() => (this.deleteInProgress = false));
    } else {
      this.delete.emit();
    }
  }

  isExistingLocale(): boolean {
    return !!this.locale.id;
  }

  private _resetTitle() {
    this._title = !_.isEmpty(this.displayName) ? this.displayName : this._toLocale().toString();
  }

  private _toNewLocale(): TranslationLocaleCreationDto {
    return {
      language: this.language,
      displayName: this.displayName,
      region: this.region,
      variants: this.variants,
      icon: this.icon,
    };
  }

  private _toUpdatedLocale(): TranslationLocale {
    return new TranslationLocale(
      this.locale.id,
      this.language,
      this.icon,
      this.displayName,
      this.region,
      this.variants
    );
  }

  private _toLocale(): Locale {
    return new Locale(this.language, this.region, this.variants);
  }

  private _resetForm() {
    this.form.controls['displayName'].setValue(this.locale.displayName);
    this.form.controls['language'].setValue(this.locale.language);
    this.form.controls['region'].setValue(this.locale.region);
    this.form.controls['variants'].setValue(!_.isEmpty(this.locale.variants) ? _.join(this.locale.variants, ' ') : '');
    this.form.controls['icon'].setValue(this.locale.icon);

    this.form.markAsPristine();
    this.form.markAsUntouched();
  }
}
