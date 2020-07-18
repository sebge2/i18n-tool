import bcp47 from "bcp-47";
import * as _ from "lodash";

export class Locale {

    public static fromString(value: string): Locale {
        if (value === null) {
            return null;
        }

        const parsed = bcp47.parse(value);

        return new Locale(parsed.language, parsed.region, parsed.variants);
    }

    constructor(public language: string,
                public region?: string,
                public variants: string[] = []) {
        this.language = _.isEmpty(this.language) ? null : this.language.toLowerCase();
        this.region = _.isEmpty(this.region) ? null : this.region.toUpperCase();
        this.variants = (this.variants !== null) ? this.variants : [];
    }

    public toString() {
        return bcp47.stringify({
            language: this.language,
            region: this.region,
            variants: this.variants
        });
    }

    public matchLanguage(other: Locale): boolean {
        if (other === null) {
            return false;
        }

        return other.language === this.language;
    }

    public matchLanguageAndRegion(other: Locale): boolean {
        if (other === null) {
            return false;
        }

        return (other.language == this.language)
            && (other.region === this.region);
    }

    public matchStrictly(other: Locale): boolean {
        if (other === null) {
            return false;
        }

        return (other.language === this.language)
            && (other.region === this.region)
            && ((_.intersection(other.variants, this.variants).length > 0)
                || ((other.variants.length == 0) && (this.variants.length == 0)));
    }

    public hasOnlyLanguage() : boolean {
        return !_.isNil(this.language) && _.isNil(this.region) && _.isEmpty(this.variants);
    }

    public hasOnlyLanguageAndRegion() : boolean {
        return !_.isNil(this.language) && !_.isNil(this.region) && _.isEmpty(this.variants);
    }

    public hasLanguageAndRegionAndVariants() : boolean {
        return !_.isNil(this.language) && !_.isNil(this.region) && !_.isEmpty(this.variants);
    }
}
