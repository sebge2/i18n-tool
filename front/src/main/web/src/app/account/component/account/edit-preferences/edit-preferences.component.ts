import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';
import { ToolLocale } from '@i18n-core-translation';
import { TranslationLocale } from '@i18n-core-translation';
import { Subject } from 'rxjs';
import { UserPreferencesService } from '@i18n-core-translation';
import { NotificationService } from '@i18n-core-notification';
import { ToolLocaleService } from '@i18n-core-translation';
import { TranslationLocaleService } from '@i18n-core-translation';
import { takeUntil } from 'rxjs/operators';
import { UserPreferences } from '@i18n-core-translation';

@Component({
  selector: 'app-edit-preferences',
  templateUrl: './edit-preferences.component.html',
  styleUrls: ['./edit-preferences.component.css'],
})
export class EditPreferencesComponent implements OnInit, OnDestroy {
  form: FormGroup;

  currentToolLocale: ToolLocale;
  preferredToolLocale: ToolLocale;

  currentPreferredLocales: TranslationLocale[] = [];
  preferredLocales: TranslationLocale[] = [];

  public loading: boolean = false;

  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _userPreferencesService: UserPreferencesService,
    private _formBuilder: FormBuilder,
    private _notificationService: NotificationService,
    public toolLocaleService: ToolLocaleService,
    public translationLocaleService: TranslationLocaleService
  ) {
    this.form = _formBuilder.group({
      toolLocale: [],
      preferredLocales: [],
    });
  }

  ngOnInit() {
    this.toolLocaleService
      .getCurrentLocale()
      .pipe(takeUntil(this._destroyed$))
      .subscribe((currentToolLocale) => {
        this.currentToolLocale = currentToolLocale;
        this.selectedToolLocale = this.currentToolLocale;
      });

    this.toolLocaleService
      .getPreferredToolLocale()
      .pipe(takeUntil(this._destroyed$))
      .subscribe((preferredToolLocale) => (this.preferredToolLocale = preferredToolLocale));

    this.translationLocaleService
      .getDefaultLocales()
      .pipe(takeUntil(this._destroyed$))
      .subscribe((currentPreferredLocales) => {
        this.currentPreferredLocales = currentPreferredLocales;
        this.selectedPreferredLocales = this.currentPreferredLocales;
      });

    this.translationLocaleService
      .getPreferredLocales()
      .pipe(takeUntil(this._destroyed$))
      .subscribe((preferredLocales) => (this.preferredLocales = preferredLocales));
  }

  ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  get selectedToolLocaleForm(): AbstractControl {
    return this.form.controls['toolLocale'];
  }

  get selectedToolLocale(): ToolLocale {
    return this.selectedToolLocaleForm.value;
  }

  set selectedToolLocale(locale: ToolLocale) {
    this.selectedToolLocaleForm.setValue(locale);
  }

  get autoDetectedToolLocale(): boolean {
    return !this.preferredToolLocale && !this.selectedToolLocaleForm.dirty;
  }

  get selectedPreferredLocalesForm(): AbstractControl {
    return this.form.controls['preferredLocales'];
  }

  get selectedPreferredLocales(): TranslationLocale[] {
    return this.selectedPreferredLocalesForm.value;
  }

  set selectedPreferredLocales(preferredLocales: TranslationLocale[]) {
    this.selectedPreferredLocalesForm.setValue(preferredLocales);
  }

  get autoDetectedPreferredLocales(): boolean {
    return this.preferredLocales.length == 0 && !this.selectedPreferredLocalesForm.dirty;
  }

  onSave() {
    this.loading = true;

    this._userPreferencesService
      .updateUserPreferences(
        new UserPreferences(
          this.selectedToolLocale,
          this.selectedPreferredLocales.map((locale) => locale.id)
        )
      )
      .toPromise()
      .catch((error) => {
        console.error("Error while saving user's preferences.", error);
        this._notificationService.displayErrorMessage("Error while saving user's preferences.");
      })
      .finally(() => {
        this.form.markAsPristine();
        this.loading = false;
      });
  }

  resetForm() {
    this.selectedToolLocale = this.currentToolLocale;
    this.selectedToolLocaleForm.markAsPristine();

    this.selectedPreferredLocales = this.currentPreferredLocales;
    this.selectedPreferredLocalesForm.markAsPristine();
  }
}
