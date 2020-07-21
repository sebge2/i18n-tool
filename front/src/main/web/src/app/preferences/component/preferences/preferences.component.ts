import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserPreferencesService} from "../../service/user-preferences.service";
import {ToolLocaleService} from "../../../core/translation/service/tool-locale.service";
import {ToolLocale} from "../../../core/translation/model/tool-locale.model";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {FormBuilder, FormGroup} from "@angular/forms";
import {UserPreferences} from "../../model/user-preferences";
import {TranslationLocaleService} from "../../../translations/service/translation-locale.service";
import {TranslationLocale} from "../../../translations/model/translation-locale.model";

@Component({
    selector: 'app-preferences',
    templateUrl: './preferences.component.html',
    styleUrls: ['./preferences.component.css']
})
export class PreferencesComponent implements OnInit, OnDestroy {

    public form: FormGroup;

    public preferredToolLocale: ToolLocale;
    public preferredLocales: TranslationLocale[] = [];
    public loading: boolean = false;

    private readonly _destroyed$ = new Subject();

    constructor(private userPreferencesService: UserPreferencesService,
                private formBuilder: FormBuilder,
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
            .subscribe(preferredLocales => this.form.controls['preferredLocales'].setValue(preferredLocales));

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
            .updateUserPreferences(new UserPreferences(this.toolLocale, this.form.controls['preferredLocales'].value))
            .toPromise() // TODO error
            .finally(() => {
                this.form.markAsPristine();
                this.loading = false;
            });
    }
}
