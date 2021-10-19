export class TranslationKey {

    public static forKey(key: string): TranslationKey {
        return new TranslationKey(key);
    }

    constructor(public key: string) {
    }
}