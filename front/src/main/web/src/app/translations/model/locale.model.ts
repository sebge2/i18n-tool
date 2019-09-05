export enum Locale {

    FR = "fr",

    NL = "nl",

    EN = "en"

}

export function findLocaleFromString(value: string): Locale {
    return (<any>Locale)[value.toString().toUpperCase()];
}

export const ALL_LOCALES = Object.keys(Locale).map(key => Locale[key]);
