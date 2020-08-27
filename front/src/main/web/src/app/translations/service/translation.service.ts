import {Injectable} from '@angular/core';
import {TranslationsSearchRequest} from "../model/search/translations-search-request.model";
import {NotificationService} from "../../core/notification/service/notification.service";
import {TranslationService as ApiTranslationService} from "../../api";
import {Observable} from "rxjs";
import {TranslationsPage} from "../model/search/translations-page.model";
import {map} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class TranslationService {

    constructor(private _translationService: ApiTranslationService,
                private _notificationService: NotificationService) {
    }

    public searchTranslations(searchRequest: TranslationsSearchRequest, maxKeys: number = 500, lastPageKey?: string): Observable<TranslationsPage> {
        return this._translationService
            .searchTranslations({
                    workspaces: searchRequest.workspaces.map(workspace => workspace.workspace.id),
                    locales: searchRequest.locales.map(locale => locale.id),
                    criterion: searchRequest.criterion,
                    maxKeys: maxKeys,
                    lastPageKey: lastPageKey
                },
                'search'
            )
            .pipe(map(dto => TranslationsPage.fromDto(dto)));
    }

    public updateTranslation(bundleKeyId: string, localeId: string, translation: string): Observable<any> {
        return this._translationService.updateWorkspaceTranslations(bundleKeyId, localeId, translation);
    }

}
