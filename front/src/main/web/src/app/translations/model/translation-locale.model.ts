import bcp47 from "bcp-47";

export class TranslationLocale {

    public id: string;
    public language: string;
    public region: string;
    public variants: string[];
    public icon: string;

    constructor(locale: TranslationLocale = <TranslationLocale>{}) {
        this.id = locale.id;
        this.language = locale.language;
        this.region = locale.region;
        this.variants = locale.variants || [];
        this.icon = locale.icon;
    }

    public toString() {
        return bcp47.stringify({
            language: this.language,
            region: this.region,
            variants: this.variants
        });
    }
}
