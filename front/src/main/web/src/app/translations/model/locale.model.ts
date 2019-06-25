export enum Locale {

    FR = "fr",

    NL = "nl",

    EN = "en"

}

export function findLocaleFromString(value: string): Locale {
    return (value != null) ? (<any>Locale)[value.toString().toUpperCase()] : null;
}

export const ALL_LOCALES = Object.keys(Locale).map(key => Locale[key]);
