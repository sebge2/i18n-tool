import {Pipe, PipeTransform} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {TranslationKey} from "../model/translation-key.model";

@Pipe({
    name: 'apptranslate'
})
export class TranslatePipe implements PipeTransform {

    constructor(private _translationService: TranslateService) {
    }

    transform(value: string | TranslationKey, ...args: unknown[]): unknown {
        if (value instanceof TranslationKey) {
            return this._translationService.instant(value.key, args);
        } else {
            return value;
        }
    }

}
