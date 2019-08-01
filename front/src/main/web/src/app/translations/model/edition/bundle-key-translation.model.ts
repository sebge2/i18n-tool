import {Locale} from "../locale.model";

export class BundleKeyTranslation {

    id: String;
    lastEditor: String;
    locale: Locale;
    originalValue: String;
    updatedValue: String;

    constructor(bundleKeyTranslation: BundleKeyTranslation = <BundleKeyTranslation>{}) {
        this.id = bundleKeyTranslation.id;
        this.lastEditor = bundleKeyTranslation.lastEditor;
        this.locale = bundleKeyTranslation.locale;
        this.originalValue = bundleKeyTranslation.originalValue;
        this.updatedValue = bundleKeyTranslation.updatedValue;
    }

    currentValue(): String {
        return this.updatedValue != null ? this.updatedValue : this.originalValue;
    }
}
