import { Pipe, PipeTransform } from '@angular/core';
import { ToolLocale } from '@i18n-core-translation';

@Pipe({
  name: 'toolLocaleIcon',
})
export class ToolLocaleIconPipe implements PipeTransform {
  transform(locale: ToolLocale): String {
    return `flag-icon ${locale.icon}`;
  }
}
