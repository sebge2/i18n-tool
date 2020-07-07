import * as _ from "lodash";

export class Locale {

    public language: string;
    public region: string;
    public variants: string[];
    public icon: string;

    constructor(locale: Locale = <Locale>{}) {
        this.language = locale.language;
        this.region = locale.region;
        this.variants = locale.variants || [];
        this.icon = locale.icon;
    }
}
