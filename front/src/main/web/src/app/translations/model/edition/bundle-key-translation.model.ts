import {Locale} from "../../../core/translation/model/locale.model";

export class BundleKeyTranslation {

    id: String;
    lastEditor: String;
    locale: Locale;
    originalValue: String;
    updatedValue: String;

    constructor(bundleKeyTranslation: BundleKeyTranslation = <BundleKeyTranslation>{}) {
        Object.assign(this, bundleKeyTranslation);
    }

    currentValue(): String {
        return this.updatedValue != null ? this.updatedValue : this.originalValue;
    }
}
