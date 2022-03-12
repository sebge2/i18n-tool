import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {DictionaryEntry} from '../model/dictionary-entry.model';
import {
    DictionaryEntryCreationDto,
    DictionaryEntryDto,
    DictionaryEntryPatchDto,
    DictionaryService as ApiDictionaryService,
} from '../../api';
import {catchError, map, tap} from 'rxjs/operators';
import {ImportedFile, PreferencesService, SynchronizedCollection} from '@i18n-core-shared';
import {Events, EventService} from '@i18n-core-event';
import {NotificationService} from '@i18n-core-notification';
import * as _ from 'lodash';
import {HttpResponse} from '@angular/common/http';
import {TranslationLocale, TranslationLocaleService} from '@i18n-core-translation';

const PREFERRED_LOCALES_STORAGE_KEY = 'dictionary-service-preferred-locales';

@Injectable({
    providedIn: 'root',
})
export class DictionaryService {
    private readonly _synchronizedEntries: SynchronizedCollection<DictionaryEntryDto, DictionaryEntry>;
    private readonly _entries$: Observable<DictionaryEntry[]>;
    private readonly _locales$: Observable<TranslationLocale[]>;
    private readonly _preferredLocales$ = new BehaviorSubject<string[]>(null);

    constructor(
        private _apiDictionaryService: ApiDictionaryService,
        private _eventService: EventService,
        private _notificationService: NotificationService,
        private _localeService: TranslationLocaleService,
        private _preferenceService: PreferencesService
    ) {
        // NICE avoid to load all the collection and use the search method
        this._synchronizedEntries = new SynchronizedCollection<DictionaryEntryDto, DictionaryEntry>(
            () => _apiDictionaryService.search(),
            this._eventService.subscribeDto(Events.ADDED_DICTIONARY_ENTRY),
            this._eventService.subscribeDto(Events.UPDATED_DICTIONARY_ENTRY),
            this._eventService.subscribeDto(Events.DELETED_DICTIONARY_ENTRY),
            this._eventService.reconnected(),
            (dto) => DictionaryEntry.fromDto(dto),
            (first, second) => first.id === second.id
        );

        this._entries$ = this._synchronizedEntries.collection.pipe(
            catchError((reason) => {
                console.error('Error while retrieving dictionary entries.', reason);
                this._notificationService.displayErrorMessage('ADMIN.DICTIONARY.ERROR.GET_ALL');
                return [];
            })
        );

        this._preferenceService
            .getPreference(PREFERRED_LOCALES_STORAGE_KEY)
            .toPromise()
            .then((preferredLocaleIds: string[]) => this._preferredLocales$.next(preferredLocaleIds));

        this._locales$ = combineLatest([
            this._localeService.getAvailableLocales(),
            this._localeService.getDefaultLocales(),
            this._preferredLocales$,
        ]).pipe(
            map(([allLocales, defaultLocales, preferredLocales]) => {
                if (_.isNil(preferredLocales)) {
                    return defaultLocales;
                } else {
                    return _.chain(preferredLocales)
                        .map((preferredLocaleId) => _.find(allLocales, (locale) => _.eq(preferredLocaleId, locale.id)))
                        .filter((locale) => !_.isNil(locale))
                        .value();
                }
            })
        );
    }

    getDictionary$(): Observable<DictionaryEntry[]> {
        return this._entries$;
    }

    createTranslation(request: DictionaryEntryCreationDto): Observable<DictionaryEntry> {
        return this._apiDictionaryService
            .create3(request)
            .pipe(map((dto: DictionaryEntryDto) => DictionaryEntry.fromDto(dto)));
    }

    updateTranslations(patches: DictionaryEntryPatchDto[]): Observable<DictionaryEntry[]> {
        return this._apiDictionaryService
            .updateEntries(patches)
            .pipe(map((entries: DictionaryEntryDto[]) => _.map(entries, (entry) => DictionaryEntry.fromDto(entry))));
    }

    delete(entryId: string): Observable<any> {
        return this._apiDictionaryService.delete4(entryId);
    }

    download(): Observable<HttpResponse<Blob>> {
        return this._apiDictionaryService.exportDictionary('export', 'response');
    }

    upload(file: ImportedFile): Observable<DictionaryEntry[]> {
        return this._apiDictionaryService
            .importDictionary(file.file, 'import')
            .pipe(map((entries: DictionaryEntryDto[]) => _.map(entries, (entry) => DictionaryEntry.fromDto(entry))));
    }

    deleteAll(): Observable<any> {
        return this._apiDictionaryService.deleteAll();
    }

    getLocales$(): Observable<TranslationLocale[]> {
        return this._locales$;
    }

    getAvailableLocales$(): Observable<TranslationLocale[]> {
        return this._localeService.getAvailableLocales();
    }

    setPreferredLocales(preferredLocales: TranslationLocale[]): Observable<TranslationLocale[]> {
        const preferredLocaleIds = _.map(preferredLocales, (locale) => locale.id);

        return this._preferenceService.setPreference(PREFERRED_LOCALES_STORAGE_KEY, preferredLocaleIds).pipe(
            tap((_) => this._preferredLocales$.next(preferredLocaleIds)),
            map((_) => preferredLocales)
        );
    }
}
