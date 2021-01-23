import {LocalizedStringDto} from "../../../api";
import {DEFAULT_LOCALE} from "../../translation/model/tool-locale.model";

export class LocalizedString {

    public static fromDto(dto: LocalizedStringDto): LocalizedString {
        if (!dto) {
            return null;
        }

        const translations = new Map<string, string>();

        Object.keys(dto).forEach(key => translations.set(key, dto[key]));

        return new LocalizedString(translations)
    }

    constructor(private _translations: Map<string, string>) {
    }

    public getTranslation(locale: string): string | null {
        return this._translations.get(locale);
    }

    public getTranslationOrFallback(locale: string): string {
        return this.getTranslation(locale) || this.getTranslation(DEFAULT_LOCALE.toString()) || '';
    }
}
