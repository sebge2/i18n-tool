import bcp47 from "bcp-47";
import * as _ from "lodash";

export class Locale {

    public static fromString(value: string): Locale {
        const parsed = bcp47.parse(value);

        return new Locale(parsed.language, parsed.region, parsed.variants);
    }

    constructor(public language: string,
                public region?: string,
                public variants: string[] = []) {
    }

    public toString() {
        return bcp47.stringify({
            language: this.language,
            region: this.region,
            variants: this.variants
        });
    }

    public matchLanguage(locale: Locale): boolean {
        return locale.language == this.language;
    }

    public matchLanguageAndRegion(locale: Locale): boolean {
        return (locale.language == this.language)
            && (locale.region === this.region);
    }

    public matchStrictly(locale: Locale): boolean {
        return (locale.language === this.language)
            && (locale.region === this.region)
            && (_.intersection(locale.variants, this.variants).length > 0);
    }

}
