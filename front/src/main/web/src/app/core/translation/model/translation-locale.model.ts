import {TranslationLocaleDto} from '../../../api';
import {Locale} from './locale.model';
import * as _ from "lodash";

export class TranslationLocale {
    static fromDto(locale: TranslationLocaleDto) {
        return new TranslationLocale(
            locale.id,
            locale.language,
            locale.icon,
            locale.displayName,
            locale.region,
            locale.variants
        );
    }

    static create(): TranslationLocale {
        return new TranslationLocale(null, null, null, null, null, []);
    }

    constructor(
        public id: string,
        public language: string,
        public icon: string,
        public displayName?: string,
        public region?: string,
        public variants: string[] = []
    ) {
    }

    toLocale(): Locale {
        return new Locale(this.language, this.region, this.variants);
    }

    equals(other: TranslationLocale): boolean {
        return !_.isNil(other) && (this.id === other.id);
    }

    toString() {
        return this.displayName ? this.displayName : this.toLocale().toString();
    }

    toDto(): TranslationLocaleDto {
        return {
            id: this.id,
            language: this.language,
            icon: this.icon,
            displayName: this.displayName,
            region: this.region,
            variants: this.variants,
        };
    }
}
