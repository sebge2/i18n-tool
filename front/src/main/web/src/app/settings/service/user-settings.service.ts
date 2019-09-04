import {Injectable} from '@angular/core';
import {ALL_LOCALES, Locale} from "../../translations/model/locale.model";

@Injectable({
    providedIn: 'root'
})
export class UserSettingsService {

    constructor() {
    }

    getUserLocales(): Locale[] {
        return this.getBrowserLanguagePreferences();
    }

    getBrowserLanguagePreferences(): Locale[] {
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
