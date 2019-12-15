import {Pipe, PipeTransform} from '@angular/core';
import {Locale} from "../../../translations/model/locale.model";

@Pipe({
    name: 'localeIcon'
})
export class LocaleIconPipe implements PipeTransform {

    transform(locale: Locale): String {
        return `flag-icon ${locale.icon}`;
    }

}
