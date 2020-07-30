import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ToolLocale} from "../../../../core/translation/model/tool-locale.model";
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {Subject} from "rxjs";
import {UserPreferencesService} from "../../../service/user-preferences.service";
import {NotificationService} from "../../../../core/notification/service/notification.service";
import {ToolLocaleService} from "../../../../core/translation/service/tool-locale.service";
import {TranslationLocaleService} from "../../../../translations/service/translation-locale.service";
import {takeUntil} from "rxjs/operators";
import {UserPreferences} from "../../../model/user-preferences";

@Component({
  selector: 'app-edit-preferences',
  templateUrl: './edit-preferences.component.html',
  styleUrls: ['./edit-preferences.component.css']
})
export class EditPreferencesComponent implements OnInit, OnDestroy {

  public form: FormGroup;

  public preferredToolLocale: ToolLocale;
  public preferredLocales: TranslationLocale[] = [];
  public loading: boolean = false;

  private readonly _destroyed$ = new Subject();

  constructor(private userPreferencesService: UserPreferencesService,
              private formBuilder: FormBuilder,
              private notificationService: NotificationService,
              public toolLocaleService: ToolLocaleService,
              public translationLocaleService: TranslationLocaleService) {
    this.form = formBuilder.group({
      toolLocale: [],
      preferredLocales: []
    });
  }

  ngOnInit() {
    this.toolLocaleService
        .getCurrentLocale()
        .pipe(takeUntil(this._destroyed$))
        .subscribe(toolLocale => this.form.controls['toolLocale'].setValue(toolLocale));

    this.toolLocaleService
        .getPreferredToolLocale()
        .pipe(takeUntil(this._destroyed$))
        .subscribe(toolLocale => this.preferredToolLocale = toolLocale);

    this.translationLocaleService
        .getDefaultLocales()
        .pipe(takeUntil(this._destroyed$))
        .subscribe(defaultLocales => this.form.controls['preferredLocales'].setValue(defaultLocales));

    this.translationLocaleService
        .getPreferredLocales()
        .pipe(takeUntil(this._destroyed$))
        .subscribe(preferredLocales => this.preferredLocales = preferredLocales);
  }

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  get toolLocale(): ToolLocale {
    return this.form.controls['toolLocale'].value;
  }

  onSave() {
    this.loading = true;

    this.userPreferencesService
        .updateUserPreferences(
            new UserPreferences(
                this.toolLocale,
                this.form.controls['preferredLocales'].value.map(locale => locale.id)
            )
        )
        .toPromise()
        .catch(error => {
          console.error('Error while saving user\'s preferences.', error);
          this.notificationService.displayErrorMessage('Error while saving user\'s preferences.');
        })
        .finally(() => {
          this.form.markAsPristine();
          this.loading = false;
        });
  }

    resetForm() {

    }
}
