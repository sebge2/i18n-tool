import {Locale} from "./locale.model";

export class ToolLocale {

    constructor(public language: string, public icon: string) {
    }

    public toLocale(): Locale {
        return new Locale(this.language);
    }

    public toString(): string {
        return this.toLocale().toString();
    }
}

export const EN_LOCALE = new ToolLocale('en', 'flag-icon-gb');
export const FR_LOCALE = new ToolLocale('fr', 'flag-icon-fr');
export const DEFAULT_LOCALE = EN_LOCALE;

export const ALL_LOCALES = [EN_LOCALE, FR_LOCALE];
