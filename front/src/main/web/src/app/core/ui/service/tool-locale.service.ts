import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from "rxjs";
import {ALL_LOCALES, DEFAULT_LOCALE, ToolLocale} from "../../translation/model/tool-locale.model";
import {TranslateService} from "@ngx-translate/core";
import {UserPreferencesService} from "../../../preferences/service/user-preferences.service";
import {flatMap, map} from "rxjs/operators";
import {ActivatedRoute, Params} from "@angular/router";
import {Locale} from "../../translation/model/locale.model";

@Injectable({
    providedIn: 'root'
})
export class ToolLocaleService {

    public static FORCE_LOCALE = 'forceLocale';

    private readonly _currentLocale$: Observable<ToolLocale>;
    private readonly _forceLocale$: Observable<ToolLocale>;
    private readonly _browserLocalePreference$ = new BehaviorSubject(this.getLocaleFromBrowserPreference());

    constructor(private translateService: TranslateService,
                private preferencesServices: UserPreferencesService,
                private route: ActivatedRoute) {
        this.translateService.setDefaultLang(DEFAULT_LOCALE.toString());
        this.translateService.addLangs(ALL_LOCALES.map(locale => locale.toString()));

        this._forceLocale$ = this.route.queryParamMap
            .pipe(map((params: Params) => this.findLocaleFromString(params.get(ToolLocaleService.FORCE_LOCALE))));

        this._currentLocale$ = combineLatest([this.preferencesServices.getToolLocale(), this._forceLocale$, this._browserLocalePreference$])
            .pipe(
                map(([userPreferredLocale, forceLocale, browserLocalePreference]) => {
                    if (forceLocale != null) {
                        return forceLocale;
                    } else if (userPreferredLocale != null) {
                        return userPreferredLocale;
                    } else if (browserLocalePreference != null) {
                        return browserLocalePreference;
                    } else {
                        return DEFAULT_LOCALE;
                    }
                }),
                flatMap(locale => {
                    return this.translateService.use(locale.toString()).pipe(map(_ => {
                        return locale;
                    }))
                })
            );
    }

    getCurrentLocale(): Observable<ToolLocale> {
        return this._currentLocale$;
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
