export enum Locale {

    FR = "fr",

    NL = "nl",

    EN = "en"

}

export const ALL_LOCALES = Object.keys(Locale).map(key => Locale[key]);
