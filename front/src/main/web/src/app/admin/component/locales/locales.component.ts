import {Component, OnDestroy, OnInit} from '@angular/core';
import {TranslationLocaleService} from "../../../translations/service/translation-locale.service";
import {TranslationLocale} from "../../../translations/model/translation-locale.model";
import {BehaviorSubject, combineLatest, Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import * as _ from "lodash";

@Component({
    selector: 'app-locales',
    templateUrl: './locales.component.html',
    styleUrls: ['./locales.component.css']
})
export class LocalesComponent implements OnInit, OnDestroy {

    public locales: TranslationLocale[] = [];

    private readonly _addedLocales = new BehaviorSubject<TranslationLocale[]>([]);
    private _destroyed$ = new Subject<void>();

    constructor(public translationLocaleService: TranslationLocaleService) {
    }

    public ngOnInit() {
        combineLatest([this.translationLocaleService.getAvailableLocales(), this._addedLocales])
            .pipe(takeUntil(this._destroyed$))
            .subscribe(([availableLocales, addedLocales]) => {
                this.locales = [];
                this.locales = _.concat(this.locales, availableLocales);
                this.locales = _.concat(this.locales, addedLocales);
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onAdd() {
        const locales = this._addedLocales.getValue();
        locales.push(new TranslationLocale(null, null, null, null, null, []));

        this._addedLocales.next(locales);
    }

    public onSave(locale: TranslationLocale) {
        this.removeFromAddedLocales(locale);
    }

    public onDelete(locale: TranslationLocale) {
        this.removeFromAddedLocales(locale);
    }

    private removeFromAddedLocales(locale: TranslationLocale) {
        const locales = this._addedLocales.getValue();
        const indexOf = locales.indexOf(locale);

        if (indexOf >= 0) {
            locales.splice(indexOf, 1);
            this._addedLocales.next(locales);
        }
    }
}
