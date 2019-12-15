import bcp47 from "bcp-47";
import * as _ from "lodash";

export class ToolLocale {

    public language: string;
    public region: string;
    public variants: string[];
    public icon: string;

    constructor(locale: ToolLocale = <ToolLocale>{}) {
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

    public matchLanguageBcp47(localeString: string): boolean {
        const parsed = bcp47.parse(localeString);

        return parsed.language == this.language;
    }

    public matchStrictlyBcp47(localeString: string): boolean {
        const parsed = bcp47.parse(localeString);

        return (parsed.language === this.language)
            && (parsed.region === this.region)
            && (_.intersection(parsed.variants, this.variants).length > 0);
    }
}
