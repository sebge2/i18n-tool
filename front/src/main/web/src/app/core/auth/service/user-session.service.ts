import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../event/service/event.service";
import {UserSession} from "../model/user-session.model";
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {takeUntil} from "rxjs/operators";
import {Events} from '../../event/model/model.events.model';
import {NotificationService} from "../../notification/service/notification.service";

@Injectable({
    providedIn: 'root'
})
export class UserSessionService implements OnDestroy {

    private _userSessions: BehaviorSubject<UserSession[]> = new BehaviorSubject<UserSession[]>([]);
    private destroy$ = new Subject();

    constructor(private httpClient: HttpClient,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this.httpClient.get<UserSession[]>('/api/user-session/current').toPromise()
            .then(userSessions => this._userSessions.next(userSessions.map(userSession => new UserSession(userSession))))
            .catch(reason => this.notificationService.displayErrorMessage("Error while retrieving current sessions.", reason));

        this.eventService.subscribe(Events.CONNECTED_USER_SESSION, UserSession)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (userSession: UserSession) => {
                    let userSessions = this._userSessions.getValue().slice();
                    userSessions.push(userSession);

                    this._userSessions.next(userSessions);
                }
            );

        this.eventService.subscribe(Events.DISCONNECTED_USER_SESSION, UserSession)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (userSession: UserSession) => {
                    let userSessions = this._userSessions.getValue().slice();
                    userSessions.splice(userSessions.findIndex(currentUser => currentUser.id === userSession.id), 1);

                    this._userSessions.next(userSessions);
                }
            );
    }

    getCurrentUserSessions(): Observable<UserSession[]> {
        return this._userSessions;
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

}
