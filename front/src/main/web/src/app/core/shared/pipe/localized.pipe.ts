import {Pipe, PipeTransform} from '@angular/core';
import {LocalizedString} from "../model/localized-string.model";
import {ToolLocaleService} from "../../translation/service/tool-locale.service";
import {map} from "rxjs/operators";
import {Observable, of} from "rxjs";

@Pipe({
    name: 'localized'
})
export class LocalizedPipe implements PipeTransform {

    constructor(private _localeService: ToolLocaleService) {
    }

    transform(value: LocalizedString): Observable<string> {
        return this._localeService.getCurrentLocale()
            .pipe(map(currentLocale => value.getTranslationOrFallback(currentLocale.toString())));
    }

}
