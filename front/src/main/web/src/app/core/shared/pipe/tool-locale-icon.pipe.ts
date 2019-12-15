import {Pipe, PipeTransform} from '@angular/core';
import {ToolLocale} from "../../translation/model/tool-locale.model";

@Pipe({
    name: 'toolLocaleIcon'
})
export class ToolLocaleIconPipe implements PipeTransform {

    transform(locale: ToolLocale): String {
        return `flag-icon ${locale.icon}`;
    }

}
