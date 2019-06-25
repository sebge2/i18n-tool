import {BundleKeyTranslation} from "./bundle-key-translation.model";
import {Locale} from "../locale.model";

export class BundleKey {

    id: String;
    key: String;
    translations: BundleKeyTranslation[];

    constructor(bundleKey: BundleKey = <BundleKey>{}) {
        Object.assign(this, bundleKey);
        this.translations = bundleKey.translations.map(translation => new BundleKeyTranslation(translation));
    }

    findTranslation(locale: Locale): BundleKeyTranslation {
        const index = this.translations.findIndex(translation => translation.locale.toString().toLocaleLowerCase() == locale.toString().toLocaleLowerCase());

        if (index >= 0) {
            return this.translations[index];
        } else {
            return null;
        }
    }
}
