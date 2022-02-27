import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Events, EventService} from '@i18n-core-event';
import {UserSession} from '../model/user-session.model';
import {BehaviorSubject, Observable} from 'rxjs';
import {NotificationService} from '@i18n-core-notification';

@Injectable({
    providedIn: 'root',
})
export class UserSessionService {
    private _userSessions: BehaviorSubject<UserSession[]> = new BehaviorSubject<UserSession[]>([]);

    constructor(
        private httpClient: HttpClient,
        private eventService: EventService,
        private notificationService: NotificationService
    ) {
        this.httpClient
            .get<UserSession[]>('/api/user-session/current')
            .toPromise()
            .then((userSessions) => this._userSessions.next(userSessions.map((userSession) => new UserSession(userSession))))
            .catch((reason) => {
                console.error('Error while retrieving current sessions.', reason);
                this.notificationService.displayErrorMessage('Error while retrieving current sessions.');
            });

        this.eventService
            .subscribe(Events.CONNECTED_USER_SESSION, UserSession)
            .subscribe((userSession: UserSession) => {
                let userSessions = this._userSessions.getValue().slice();
                userSessions.push(userSession);

                this._userSessions.next(userSessions);
            });

        this.eventService
            .subscribe(Events.DISCONNECTED_USER_SESSION, UserSession)
            .subscribe((userSession: UserSession) => {
                let userSessions = this._userSessions.getValue().slice();
                userSessions.splice(
                    userSessions.findIndex((currentUser) => currentUser.id === userSession.id),
                    1
                );

                this._userSessions.next(userSessions);
            });
    }

    getCurrentUserSessions(): Observable<UserSession[]> {
        return this._userSessions;
    }
}
