import {TranslationLocaleDto} from "../../api";
import {Locale} from "../../core/translation/model/locale.model";

export class TranslationLocale {

    public static fromDto(locale: TranslationLocaleDto) {
        return new TranslationLocale(
            locale.id,
            locale.language,
            locale.icon,
            locale.displayName,
            locale.region,
            locale.variants
        );
    }

    constructor(public id: string,
                public language: string,
                public icon: string,
                public displayName?: string,
                public region?: string,
                public variants: string[] = []) {
    }

    public toLocale(): Locale {
        return new Locale(this.language, this.region, this.variants);
    }

    public equals(other: TranslationLocale): boolean {
        return this.id === other.id;
    }

    public toString() {
        return this.displayName ? this.displayName : this.toLocale().toString();
    }

    public toDto(): TranslationLocaleDto {
        return {
            id: this.id,
            language: this.language,
            icon: this.icon,
            displayName: this.displayName,
            region: this.region,
            variants: this.variants
        }
    }
}
