export class BundleKeyTranslation {

    constructor(public lastEditor: String,
                public originalValue: String,
                public updatedValue: String) {
    }

    public get currentValue(): String {
        return this.updatedValue != null ? this.updatedValue : this.originalValue;
    }
}
