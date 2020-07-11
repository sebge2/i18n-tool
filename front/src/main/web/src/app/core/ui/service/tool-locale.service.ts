import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject} from "rxjs";
import {ALL_LOCALES, DEFAULT_LOCALE, ToolLocale} from "../../translation/model/tool-locale.model";
import {TranslateService} from "@ngx-translate/core";
import {UserSettingsService} from "../../../settings/service/user-settings.service";
import {map} from "rxjs/operators";
import {ActivatedRoute, Params} from "@angular/router";
import {Locale} from "../../translation/model/locale.model";

@Injectable({
    providedIn: 'root'
})
export class ToolLocaleService {

    public static FORCE_LOCALE = 'forceLocale';

    private _currentLocale: Subject<ToolLocale> = new Subject();
    private _forceLocale: Subject<ToolLocale> = new BehaviorSubject(null);
    private _browserLocalePreference: Subject<ToolLocale> = new BehaviorSubject(this.getLocaleFromBrowserPreference());

    constructor(private translateService: TranslateService,
                private settingsService: UserSettingsService,
                private route: ActivatedRoute) {
    }

    public initialize() {
        this.getLocaleFromBrowserPreference();
        this.translateService.setDefaultLang(DEFAULT_LOCALE.toString());
        this.translateService.addLangs(ALL_LOCALES.map(locale => locale.toString()));

        this.getCurrentLocale()
            .subscribe((locale: ToolLocale) => {
                this.translateService.use(locale.toString());
            });

        combineLatest([(this.settingsService.getToolLocale()), this._forceLocale, this._browserLocalePreference])
            .subscribe(
                ([userPreferredLocale, forceLocale, browserLocalePreference]) => {
                    if (forceLocale != null) {
                        this._currentLocale.next(forceLocale);
                    } else if (userPreferredLocale != null) {
                        this._currentLocale.next(userPreferredLocale);
                    } else if (browserLocalePreference != null) {
                        this._currentLocale.next(browserLocalePreference);
                    } else {
                        this._currentLocale.next(DEFAULT_LOCALE);
                    }
                }
            );

        this.route.queryParamMap
            .pipe(map((params: Params) => this.findLocaleFromString(params.get(ToolLocaleService.FORCE_LOCALE))))
            .subscribe(forceLocale => this.forceLocale(forceLocale));
    }

    getCurrentLocale(): Observable<ToolLocale> {
        return this._currentLocale;
    }

    forceLocale(value: ToolLocale) {
        this._forceLocale.next(value);
    }

    getToolLocales(): ToolLocale[] {
        return ALL_LOCALES;
    }

    private findLocaleFromString(value: string): ToolLocale {
        return this.getToolLocales().find(locale => locale.toLocale().matchStrictly(Locale.fromString(value)));
    }

    private getLocaleFromBrowserPreference(): ToolLocale {
        for (const browserLanguage of navigator.languages) {
            for (const locale of this.getToolLocales()) {
                if (locale.toLocale().matchLanguage(Locale.fromString(browserLanguage))) {
                    return locale;
                }
            }
        }

        return null;
    }
}
