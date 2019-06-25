import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {TranslationsSearchRequest} from "../model/translations-search-request.model";
import {BundleKeysPage} from "../model/edition/bundle-keys-page.model";
import {map} from "rxjs/operators";
import {Observable} from "rxjs";
import {NotificationService} from "../../core/notification/service/notification.service";

@Injectable({
    providedIn: 'root'
})
export class TranslationsService {

    constructor(private httpClient: HttpClient,
                private notificationService: NotificationService) {
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

    updateTranslations(workspaceId: string, translations: Map<string, string>): Promise<any> {
        if (translations.size == 0) {
            return Promise.resolve();
        }

        const payload = {};
        translations.forEach((value, key) => payload[key] = value);

        return this.httpClient
            .patch('/api/workspace/' + workspaceId + '/translation', payload, {headers: {'content-type': 'application/json'}})
            .toPromise()
            .catch(reason => {
                console.error("Error while updating translations.", reason);
                this.notificationService.displayErrorMessage("Error while updating translations.")
            });
    }

}
