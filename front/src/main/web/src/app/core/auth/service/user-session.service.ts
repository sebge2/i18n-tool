import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../event/service/event.service";
import {UserSession} from "../model/user-session.model";
import {BehaviorSubject, Observable, Subscription} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class UserSessionService implements OnDestroy {

    private _connectedUserSessionObservable: Subscription;
    private _disconnectedUserSessionObservable: Subscription;
    private _userSessions: BehaviorSubject<UserSession[]> = new BehaviorSubject<UserSession[]>([]);

    constructor(private httpClient: HttpClient,
                private eventService: EventService) {
        this.httpClient.get<UserSession[]>('/api/user-session/current').toPromise()
            .then(userSessions => this._userSessions.next(userSessions))
            .catch(reason => console.error("Error while retrieving current sessions.", reason));

        this._connectedUserSessionObservable = this.eventService.subscribe("connected-user-session", UserSession)
            .subscribe(
                (userSession: UserSession) => {
                    let userSessions = this._userSessions.getValue().slice();
                    userSessions.push(userSession);

                    this._userSessions.next(userSessions);
                }
            );

        this._disconnectedUserSessionObservable = this.eventService.subscribe("disconnected-user-session", UserSession)
            .subscribe(
                (userSession: UserSession) => {
                    let userSessions = this._userSessions.getValue().slice();
                    userSessions.splice(userSessions.indexOf(userSession), 1);

                    this._userSessions.next(userSessions);
                }
            );
    }

    getCurrentUserSessions(): Observable<UserSession[]> {
        return this._userSessions;
    }

    ngOnDestroy(): void {
        this._connectedUserSessionObservable.unsubscribe();
        this._disconnectedUserSessionObservable.unsubscribe();
    }

}
