import {Injectable} from '@angular/core';
import {TranslationLocale} from "../model/translation-locale.model";
import {
    TranslationLocaleCreationDto,
    TranslationLocaleDto,
    TranslationLocaleService as ApiTranslationLocaleService
} from "../../api";
import {NotificationService} from "../../core/notification/service/notification.service";
import {combineLatest, Observable} from "rxjs";
import {Events} from "../../core/event/model/events.model";
import {catchError, distinctUntilChanged, map, tap} from "rxjs/operators";
import {EventService} from "../../core/event/service/event.service";
import {Locale} from "../../core/translation/model/locale.model";
import * as _ from "lodash";
import {UserPreferencesService} from "../../account/service/user-preferences.service";
import {SynchronizedCollection} from "../../core/shared/utils/synchronized-collection";

@Injectable({
    providedIn: 'root'
})
export class TranslationLocaleService {

    private readonly _synchronizedLocales$: SynchronizedCollection<TranslationLocaleDto, TranslationLocale>;
    private readonly _locales$: Observable<TranslationLocale[]>;

    private readonly _preferredLocales$: Observable<TranslationLocale[]>;
    private readonly _defaultLocales$: Observable<TranslationLocale[]>;
    private readonly _autoDetectedLocales$: Observable<TranslationLocale[]>;
    private readonly _browserLocales: Locale[];

    constructor(private apiService: ApiTranslationLocaleService,
                private userPreferencesService: UserPreferencesService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._synchronizedLocales$ = new SynchronizedCollection<TranslationLocaleDto, TranslationLocale>(
            () => this.apiService.findAll1(),
            this.eventService.subscribeDto(Events.ADDED_TRANSLATION_LOCALE),
            this.eventService.subscribeDto(Events.UPDATED_TRANSLATION_LOCALE),
            this.eventService.subscribeDto(Events.DELETED_TRANSLATION_LOCALE),
            this.eventService.reconnected(),
            dto => TranslationLocale.fromDto(dto),
            (first, second) => first.id === second.id
        );

        this._locales$ = this._synchronizedLocales$
            .collection
            .pipe(catchError((reason) => {
                console.error('Error while retrieving locales.', reason);
                this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.GET_ALL');
                return [];
            }));

        this._browserLocales = TranslationLocaleService.getLocalesFromBrowserPreferences();

        this._preferredLocales$ = combineLatest([this.getAvailableLocales(), this.userPreferencesService.getUserPreferences()])
            .pipe(
                map(([availableLocales, userPreferences]) =>
                    availableLocales.filter(availableLocale =>
                        _.some(
                            _.get(userPreferences, 'preferredLocales', []),
                            (preferredLocale => preferredLocale === availableLocale.id)
                        )
                    )
                ),
                distinctUntilChanged()
            );

        this._autoDetectedLocales$ = this.getAvailableLocales()
            .pipe(map(availableLocales =>
                _.uniqBy(
                    this._browserLocales
                        .map(browserLocale => TranslationLocaleService.findMatchingLocale(browserLocale, availableLocales))
                        .filter(locale => locale),
                    'id'
                )
            ));

        this._defaultLocales$ = combineLatest([this.getPreferredLocales(), this.getAutoDetectedLocales()])
            .pipe(
                map(([preferredLocales, autoDetectedLocales]) =>
                    !_.isEmpty(preferredLocales) ? preferredLocales : autoDetectedLocales
                ),
                distinctUntilChanged()
            );
    }

    public getAvailableLocales(): Observable<TranslationLocale[]> {
        return this._locales$;
    }

    public getDefaultLocales(): Observable<TranslationLocale[]> {
        return this._defaultLocales$;
    }

    public getPreferredLocales(): Observable<TranslationLocale[]> {
        return this._preferredLocales$;
    }

    public createLocale(creationTranslationLocale: TranslationLocaleCreationDto): Observable<TranslationLocale> {
        return this.apiService
            .create1(creationTranslationLocale)
            .pipe(
                map(dto => TranslationLocale.fromDto(dto)),
                tap(translationLocale => this._synchronizedLocales$.add(translationLocale))
            );
    }

    public updateLocale(translationLocale: TranslationLocale): Observable<TranslationLocale> {
        return this.apiService
            .update1(translationLocale.toDto(), translationLocale.id)
            .pipe(
                map(dto => TranslationLocale.fromDto(dto)),
                tap(translationLocale => this._synchronizedLocales$.update(translationLocale))
            );
    }

    public deleteLocale(translationLocale: TranslationLocale): Observable<TranslationLocale> {
        return this.apiService
            .delete1(translationLocale.id)
            .pipe(
                map(() => translationLocale),
                tap(translationLocale => this._synchronizedLocales$.delete(translationLocale))
            );
    }

    private static getLocalesFromBrowserPreferences(): Locale[] {
        return navigator.languages.map(browserLanguage => Locale.fromString(browserLanguage));
    }

    private static findMatchingLocale(browserLocale: Locale, translationLocales: TranslationLocale[]): TranslationLocale {
        return _.find(translationLocales, translationLocale => TranslationLocaleService.isMatchingBrowserLocale(translationLocale, browserLocale));
    }

    private static isMatchingBrowserLocale(translationLocale: TranslationLocale, browserLocale: Locale): boolean {
        const locale = translationLocale.toLocale();

        if (locale.hasOnlyLanguage()) {
            return browserLocale.matchLanguage(locale);
        } else if (locale.hasOnlyLanguageAndRegion()) {
            return browserLocale.matchLanguageAndRegion(locale);
        } else if (locale.hasLanguageAndRegionAndVariants()) {
            return browserLocale.matchStrictly(locale);
        } else {
            return false;
        }
    }

    private getAutoDetectedLocales(): Observable<TranslationLocale[]> {
        return this._autoDetectedLocales$;
    }
}
