import {Injectable} from '@angular/core';
import {ALL_LOCALES, Locale} from "../../translations/model/locale.model";
import {BehaviorSubject, Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class UserSettingsService {

    private userLocales : BehaviorSubject<Locale[]> = new BehaviorSubject<Locale[]>(UserSettingsService.getBrowserLanguagePreferences());

    constructor() {
    }

    getUserLocales(): Observable<Locale[]> {
        return this.userLocales;
    }

    private static getBrowserLanguagePreferences(): Locale[] {
        const locales = [];

        for (const browserLanguage of navigator.languages) {
            for (const locale of ALL_LOCALES) {
                if (browserLanguage.toLocaleLowerCase().startsWith(locale.toString().toLocaleLowerCase())) {
                    if (locales.indexOf(locale) < 0) {
                        locales.push(locale);
                    }
                }
            }
        }

        return locales;
    }
}
