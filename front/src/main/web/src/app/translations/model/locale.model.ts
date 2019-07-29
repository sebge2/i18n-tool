export enum Locale {

    FR = "FR",

    NL = "NL",

    EN = "EN"

}

export const ALL_LOCALES = Object.keys(Locale).map(key => Locale[key]);