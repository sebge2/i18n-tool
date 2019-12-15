import {Injectable} from '@angular/core';
import {ToolLocale} from "../../translations/model/tool-locale.model";
import {Observable, of, Subject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class UserSettingsService {

    private toolLocale = new Subject<ToolLocale>();

    constructor() {
        // TODO
    }

    getUserLocales(): Observable<ToolLocale[]> {
        return of([]);
    }

    getToolLocale(): Observable<ToolLocale> {
        return this.toolLocale;
    }
}
