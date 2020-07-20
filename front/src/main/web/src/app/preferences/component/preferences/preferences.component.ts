import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserPreferencesService} from "../../service/user-preferences.service";
import {ToolLocaleService} from "../../../core/ui/service/tool-locale.service";
import {ToolLocale} from "../../../core/translation/model/tool-locale.model";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {FormBuilder, FormGroup} from "@angular/forms";
import {UserPreferences} from "../../model/user-preferences";

@Component({
    selector: 'app-preferences',
    templateUrl: './preferences.component.html',
    styleUrls: ['./preferences.component.css']
})
export class PreferencesComponent implements OnInit, OnDestroy {

    public form: FormGroup;

    public preferredToolLocale: ToolLocale;
    public loading: boolean = false;

    private readonly _destroyed$ = new Subject();

    constructor(private userPreferencesService: UserPreferencesService,
                private formBuilder: FormBuilder,
                public toolLocaleService: ToolLocaleService) {
        this.form = formBuilder.group({
            toolLocale: [],
        });
    }

    ngOnInit() {
        this.toolLocaleService
            .getCurrentLocale()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(toolLocale => this.form.controls['toolLocale'].setValue(toolLocale));

        this.toolLocaleService
            .getToolLocale()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(toolLocale => this.preferredToolLocale = toolLocale);
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
            .updateUserPreferences(new UserPreferences(this.toolLocale, []))// TODO public translationLocaleService: TranslationLocaleService
            .toPromise()
            .finally(() => {
                this.form.markAsPristine();
                this.loading = false;
            });
    }
}
