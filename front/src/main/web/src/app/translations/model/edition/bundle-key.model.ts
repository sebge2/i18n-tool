import {BundleKeyTranslation} from "./bundle-key-translation.model";

export class BundleKey {

    id: String;
    key: String;
    translations: BundleKeyTranslation[];

    constructor(bundleKey: BundleKey = <BundleKey>{}) {
        this.id = bundleKey.id;
        this.key = bundleKey.key;
        this.translations = bundleKey.translations.map(translation => new BundleKeyTranslation(translation));
    }
}
