import {Injectable} from '@angular/core';
import {Observable, of} from "rxjs";
import {UserPreferencesDto, UserPreferencesService as ApiUserPreferencesService} from "../../api";
import {Events} from "../../core/event/model/events.model";
import {catchError, map, mergeMap, tap} from "rxjs/operators";
import {EventService} from "../../core/event/service/event.service";
import {NotificationService} from "../../core/notification/service/notification.service";
import {AuthenticationService} from "../../core/auth/service/authentication.service";
import {UserPreferences} from "../model/user-preferences";
import {SynchronizedObject} from "../../core/shared/utils/synchronized-object";

@Injectable({
    providedIn: 'root'
})
export class UserPreferencesService {

    private readonly _synchronizedUserPreferences$: SynchronizedObject<UserPreferencesDto, UserPreferences>;
    private readonly _userPreferences$: Observable<UserPreferences>;

    constructor(private apiService: ApiUserPreferencesService,
                private authService: AuthenticationService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._synchronizedUserPreferences$ = new SynchronizedObject<UserPreferencesDto, UserPreferences>(
            () => this.authService.currentAuthenticatedUser()
                .pipe(mergeMap(currentUser =>
                    currentUser ? this.apiService.getCurrentUserPreferences() : of(null)
                )),
            this.eventService.subscribeDto(Events.UPDATED_USER_PREFERENCES),
            of(),
            this.eventService.reconnected(),
            dto => (dto != null) ? UserPreferences.fromDto(dto) : null
        );

        this._userPreferences$ = this._synchronizedUserPreferences$
            .element
            .pipe(catchError((reason) => {
                console.error('Error while retrieving user preferences.', reason);
                this.notificationService.displayErrorMessage('PREFERENCES.ERROR.GET');
                return [];
            }));
    }

    public getUserPreferences(): Observable<UserPreferences> {
        return this._userPreferences$;
    }

    public updateUserPreferences(userPreferences: UserPreferences): Observable<UserPreferences> {
        return this.apiService
            .updateCurrentUserPreferences(userPreferences.toDto())
            .pipe(
                map(userPreferences => UserPreferences.fromDto(userPreferences)),
                tap(userPreferences => this._synchronizedUserPreferences$.update(userPreferences))
            );
    }

}
