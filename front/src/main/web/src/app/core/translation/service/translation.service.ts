import { Injectable } from '@angular/core';
import { TranslationsSearchRequest } from '../../../translations/model/search/translations-search-request.model';
import { NotificationService } from '@i18n-core-notification';
import { TranslationService as ApiTranslationService, TranslationUpdateDto } from '../../../api';
import { Observable } from 'rxjs';
import { TranslationsPage } from '../../../translations/model/search/translations-page.model';
import { map } from 'rxjs/operators';
import { mapToSingleton } from '@i18n-core-shared';
import { TranslationKeyPattern } from '../../../translations/model/search/translation-key-pattern.model';

@Injectable({
  providedIn: 'root',
})
export class TranslationService {
  constructor(private _translationService: ApiTranslationService, private _notificationService: NotificationService) {}

  searchTranslations(searchRequest: TranslationsSearchRequest, maxKeys: number = 500): Observable<TranslationsPage> {
    return this._translationService
      .searchTranslations(
        {
          workspaces: searchRequest.workspaces.map((workspace) => workspace.id),
          bundleFiles: mapToSingleton(searchRequest.bundleFile, 'id'),
          locales: searchRequest.locales.map((locale) => locale.id),
          criterion: searchRequest.criterion,
          maxKeys: maxKeys,
          keyPattern: TranslationKeyPattern.toDto(searchRequest.keyPattern),
          pageSpec: searchRequest.pageSpec
            ? { keyOtherPage: searchRequest.pageSpec.keyOtherPage, nextPage: searchRequest.pageSpec.nextPage }
            : null,
        },
        'search'
      )
      .pipe(map((dto) => TranslationsPage.fromDto(dto)));
  }

  updateTranslation(bundleKeyId: string, localeId: string, translation: string): Observable<any> {
    return this._translationService.updateTranslation(bundleKeyId, localeId, translation);
  }

  updateTranslations(translations: TranslationUpdateDto[]): Observable<any> {
    return this._translationService.updateTranslations(translations);
  }
}
