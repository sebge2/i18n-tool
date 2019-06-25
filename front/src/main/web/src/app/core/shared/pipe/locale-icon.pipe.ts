import {Pipe, PipeTransform} from '@angular/core';
import {findLocaleFromString, Locale} from "../../../translations/model/locale.model";

@Pipe({
    name: 'localeIcon'
})
export class LocaleIconPipe implements PipeTransform {

    transform(locale: any): String {
        const localeToUse = findLocaleFromString(locale);

        switch (localeToUse) {
            case Locale.FR:
                return "flag-icon flag-icon-fr";
            case Locale.EN:
                return "flag-icon flag-icon-gb";
            case Locale.NL:
                return "flag-icon flag-icon-nl";
            default:
                return null;
        }
    }

}
