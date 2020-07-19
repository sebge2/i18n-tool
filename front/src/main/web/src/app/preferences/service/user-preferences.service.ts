import {Injectable} from '@angular/core';
import {Observable, of} from "rxjs";
import {UserPreferencesService as ApiUserPreferencesService} from "../../api";
import {synchronizedObject} from "../../core/shared/utils/synchronized-observable-utils";
import {Events} from "../../core/event/model/events.model";
import {catchError, flatMap} from "rxjs/operators";
import {EventService} from "../../core/event/service/event.service";
import {NotificationService} from "../../core/notification/service/notification.service";
import {AuthenticationService} from "../../core/auth/service/authentication.service";
import {UserPreferences} from "../model/user-preferences";

@Injectable({
    providedIn: 'root'
})
export class UserPreferencesService {

    private readonly _userPreferences$: Observable<UserPreferences>;

    constructor(private apiService: ApiUserPreferencesService,
                private authService: AuthenticationService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._userPreferences$ = synchronizedObject(
            this.authService.currentUser().pipe(flatMap(currentUser => currentUser ? this.apiService.getCurrentUserPreferences() : of(null))),
            this.eventService.subscribeDto(Events.UPDATED_USER_PREFERENCES),
            of(),
            dto => (dto != null) ? new UserPreferences(dto) : null
        )
            .pipe(catchError((reason) => {
                console.error("Error while retrieving user preferences.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving user preferences.");
                return [];
            }));
    }

    getUserPreferences(): Observable<UserPreferences> {
        return this._userPreferences$;
    }

}
