import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {TranslationsSearchRequest} from "../model/translations-search-request.model";
import {BundleKeysPage} from "../model/edition/bundle-keys-page.model";
import {map} from "rxjs/operators";
import {Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class TranslationsService {

    constructor(private httpClient: HttpClient) {
    }

    getTranslations(searchRequest: TranslationsSearchRequest,
                    lastPage?: BundleKeysPage,
                    maxKeys?: number): Observable<BundleKeysPage> {
        let params = new HttpParams();

        if (searchRequest.criterion != null) {
            params = params.set("criterion", searchRequest.criterion);
        }

        if (lastPage != null) {
            params = params.set("lastKey", String(lastPage.lastKey));
        }

        if (maxKeys != null) {
            params = params.set("maxKeys", String(maxKeys));
        }

        for (const locale of searchRequest.usedLocales()) {
            params = params.append("locales", locale);
        }

        return this.httpClient
            .get<BundleKeysPage>(
                '/api/workspace/' + searchRequest.workspace.id + '/translation',
                {params: params}
            )
            .pipe(map(page => new BundleKeysPage(page)));
    }

}
