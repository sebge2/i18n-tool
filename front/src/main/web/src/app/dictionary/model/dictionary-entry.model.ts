import {DictionaryEntryDto} from "../../api";
import {TranslationLocale} from "../../translations/model/translation-locale.model";
import * as _ from "lodash";

export class DictionaryEntry {

    public static fromDto(entryDto: DictionaryEntryDto): DictionaryEntry {
        return new DictionaryEntry(entryDto.id, entryDto.translations);
    }

    constructor(public id: string,
                private _translations: { [key: string]: string }) {
    }

    public get localeIds(): string[] {
        return _.keys(this._translations)
    }

    public getTranslationForLocaleEntity(locale: TranslationLocale): string | undefined {
        return this.getTranslationForLocale(locale.id);
    }

    public getTranslationForLocale(locale: string): string | undefined {
        return this._translations[locale];
    }

}