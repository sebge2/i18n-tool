import {Pipe, PipeTransform} from '@angular/core';
import {TranslationLocale} from "../../../translations/model/translation-locale.model";

@Pipe({
    name: 'translationLocaleIcon'
})
export class TranslationLocaleIconPipe implements PipeTransform {

    transform(locale: TranslationLocale): String {
        return `flag-icon ${locale?.icon}`;
    }

}
