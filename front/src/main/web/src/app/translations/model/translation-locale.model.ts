import bcp47 from "bcp-47";
import {TranslationLocaleDto} from "../../api";
import {Locale} from "../../core/translation/model/locale.model";

export class TranslationLocale {

    public static from(locale: TranslationLocale) { // TODO
        return new TranslationLocale(null);
    }

    constructor(private dto: TranslationLocaleDto) {
    }

    public get id(): string {
        return this.dto.id;
    }

    public get language(): string {
        return this.dto.language;
    }

    public get region(): string | null {
        return this.dto.region;
    }

    public get variants(): string[] {
        return this.dto.variants || [];
    }

    public get icon(): string {
        return this.dto.icon;
    }

    public get displayName(): string {
        return this.toLocale().toString(); // TODO
    }

    public toLocale(): Locale {
        return new Locale(this.language, this.region, this.variants);
    }

    public equals(other: TranslationLocale): boolean {
        return this.id === other.id;
    }

    public toString() {
        return bcp47.stringify({
            language: this.language,
            region: this.region,
            variants: this.variants
        });
    }
}
