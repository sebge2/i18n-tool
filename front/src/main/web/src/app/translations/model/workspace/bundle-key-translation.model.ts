import {TranslationsPageTranslation} from "../search/translations-page-translation.model";

export class BundleKeyTranslation {

    public static fromPage(translation: TranslationsPageTranslation): BundleKeyTranslation {
        return new BundleKeyTranslation(
            translation.lastEditor,
            translation.originalValue,
            translation.updatedValue
        );
    }

    constructor(public lastEditor: String,
                public originalValue: String,
                public updatedValue: String) {
    }

    public get currentValue(): String {
        return this.updatedValue != null ? this.updatedValue : this.originalValue;
    }
}
