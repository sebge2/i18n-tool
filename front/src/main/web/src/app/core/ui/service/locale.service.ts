import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject} from "rxjs";
import {ALL_LOCALES, Locale} from "../../../translations/model/locale.model";
import {TranslateService} from "@ngx-translate/core";
import {UserSettingsService} from "../../../settings/service/user-settings.service";
import {map} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class LocaleService {

    private _currentLocale: Subject<Locale> = new Subject();
    private _forceLocale: Subject<Locale> = new BehaviorSubject(null);

    constructor(private translateService: TranslateService,
                private settingsService: UserSettingsService) {
        this.initializeTranslationService();
    }

    public initializeTranslationService() {
        this.translateService.setDefaultLang(Locale.EN.toString());
        this.translateService.addLangs(ALL_LOCALES.map(locale => locale.toString()));

        this.currentLocale
            .subscribe((locale: Locale) => {
                this.translateService.use(locale);
            });

        combineLatest([this.settingsService.getUserLocales().pipe(map(locales => (locales.length > 0) ? locales[0] : null)), this._forceLocale])
            .subscribe(
                ([settingLocale, forceLocale]) => {
                    if (forceLocale != null) {
                        this._currentLocale.next(forceLocale);
                    } else if (settingLocale != null) {
                        this._currentLocale.next(settingLocale);
                    } else {
                        this._currentLocale.next(Locale.EN);
                    }
                }
            );
    }

    get currentLocale(): Observable<Locale> {
        return this._currentLocale;
    }

    set forceLocale(value: Locale) {
        this._forceLocale.next(value);
    }
}
