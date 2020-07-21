import {Injectable} from '@angular/core';
import {TranslationLocale} from "../model/translation-locale.model";
import {TranslationLocaleService as ApiTranslationLocaleService} from "../../api";
import {NotificationService} from "../../core/notification/service/notification.service";
import {combineLatest, Observable} from "rxjs";
import {synchronizedCollection} from "../../core/shared/utils/synchronized-observable-utils";
import {Events} from "../../core/event/model/events.model";
import {catchError, distinctUntilChanged, map} from "rxjs/operators";
import {EventService} from "../../core/event/service/event.service";
import {Locale} from "../../core/translation/model/locale.model";
import * as _ from "lodash";
import {UserPreferencesService} from "../../preferences/service/user-preferences.service";

@Injectable({
    providedIn: 'root'
})
export class TranslationLocaleService {

    private readonly _availableLocales$: Observable<TranslationLocale[]>;
    private readonly _preferredLocales$: Observable<TranslationLocale[]>;
    private readonly _defaultLocales$: Observable<TranslationLocale[]>;
    private readonly _autoDetectedLocales$: Observable<TranslationLocale[]>;
    private readonly _browserLocales: Locale[];

    constructor(private apiService: ApiTranslationLocaleService,
                private userPreferencesService: UserPreferencesService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._availableLocales$ = synchronizedCollection(
            this.apiService.findAll1(),
            this.eventService.subscribeDto(Events.ADDED_TRANSLATION_LOCALE),
            this.eventService.subscribeDto(Events.UPDATED_TRANSLATION_LOCALE),
            this.eventService.subscribeDto(Events.DELETED_TRANSLATION_LOCALE),
            dto => new TranslationLocale(dto),
            (first, second) => first.id === second.id
        )
            .pipe(catchError((reason) => {
                console.error("Error while retrieving locales.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving available locales.");
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
                availableLocales.filter(availableLocale => this.isMatchingBrowserLocale(availableLocale, this._browserLocales))
            ));

        this._defaultLocales$ = combineLatest([this.getPreferredLocales(), this.getAutoDetectedLocales()])
            .pipe(
                map(([preferredLocales, autoDetectedLocales]) =>
                    !_.isEmpty(preferredLocales) ? preferredLocales : autoDetectedLocales
                ),
                distinctUntilChanged()
            );
    }

    getAvailableLocales(): Observable<TranslationLocale[]> {
        return this._availableLocales$;
    }

    getDefaultLocales(): Observable<TranslationLocale[]> {
        return this._defaultLocales$;
    }

    getPreferredLocales(): Observable<TranslationLocale[]> {
        return this._preferredLocales$;
    }

    private static getLocalesFromBrowserPreferences(): Locale[] {
        return navigator.languages.map(browserLanguage => Locale.fromString(browserLanguage));
    }

    private getAutoDetectedLocales(): Observable<TranslationLocale[]> {
        return this._autoDetectedLocales$;
    }

    private isMatchingBrowserLocale(availableTranslationLocale: TranslationLocale, browserLocales: Locale[]): boolean {
        const availableLocale = availableTranslationLocale.toLocale();

        if (availableLocale.hasOnlyLanguage()) {
            return _.some(browserLocales, browserLocale => browserLocale.matchLanguage(availableLocale));
        } else if (availableLocale.hasOnlyLanguageAndRegion()) {
            return _.some(browserLocales, browserLocale => browserLocale.matchLanguageAndRegion(availableLocale));
        } else if (availableLocale.hasLanguageAndRegionAndVariants()) {
            return _.some(browserLocales, browserLocale => browserLocale.matchStrictly(availableLocale));
        } else {
            return false;
        }
    }
}
