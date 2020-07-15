import {Injectable} from '@angular/core';
import {ToolLocale} from "../../core/translation/model/tool-locale.model";
import {combineLatest, Observable, of} from "rxjs";
import {UserPreferencesService as ApiUserPreferencesService} from "../../api";
import {synchronizedObject} from "../../core/shared/utils/synchronized-observable-utils";
import {Events} from "../../core/event/model/events.model";
import {TranslationLocale} from "../../translations/model/translation-locale.model";
import {catchError, distinctUntilChanged, flatMap, map} from "rxjs/operators";
import {EventService} from "../../core/event/service/event.service";
import {NotificationService} from "../../core/notification/service/notification.service";
import {AuthenticationService} from "../../core/auth/service/authentication.service";
import {UserPreferences} from "../model/user-preferences";
import {TranslationLocaleService} from "../../translations/service/translation-locale.service";
import * as _ from "lodash";

@Injectable({
    providedIn: 'root'
})
export class UserPreferencesService {

    private readonly _userPreferences$: Observable<UserPreferences>;
    private readonly _toolLocale$: Observable<ToolLocale>;
    private readonly _preferredLocales$: Observable<TranslationLocale[]>;

    constructor(private apiService: ApiUserPreferencesService,
                private authService: AuthenticationService,
                private translationLocaleService: TranslationLocaleService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._userPreferences$ = synchronizedObject(
            this.authService.currentUser().pipe(flatMap(currentUser => this.apiService.getPreferencesByUserId(currentUser.user.id))),
            this.eventService.subscribeDto(Events.UPDATED_TRANSLATION_LOCALE),
            of(),
            dto => new UserPreferences(dto)
        )
            .pipe(catchError((reason) => {
                console.error("Error while retrieving user preferences.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving user preferences.");
                return [];
            }));

        this._toolLocale$ = this._userPreferences$
            .pipe(
                map(preferences => preferences.toolLocale),
                distinctUntilChanged()
            );

        this._preferredLocales$ = combineLatest([this.translationLocaleService.getAvailableLocales(), this._userPreferences$])
            .pipe(
                map(([availableLocales, userPreferences]) =>
                    availableLocales.filter(availableLocale => _.some(userPreferences.preferredLocales, availableLocale))
                ),
                distinctUntilChanged()
            );
    }

    getUserPreferences(): Observable<UserPreferences> {
        return this._userPreferences$;
    }

    getPreferredLocales(): Observable<TranslationLocale[]> {
        return this._preferredLocales$;
    }

    getToolLocale(): Observable<ToolLocale> {
        return this._toolLocale$;
    }
}
