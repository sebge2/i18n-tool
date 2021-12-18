import { Pipe, PipeTransform } from '@angular/core';
import { TranslationLocale } from '@i18n-core-translation';

@Pipe({
  name: 'translationLocaleIcon',
})
export class TranslationLocaleIconPipe implements PipeTransform {
  transform(locale: TranslationLocale): String {
    return `flag-icon ${locale?.icon}`;
  }
}
