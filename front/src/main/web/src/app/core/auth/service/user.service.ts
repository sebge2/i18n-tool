import {Injectable, OnDestroy} from '@angular/core';
import {BehaviorSubject, Observable, Subject, throwError} from "rxjs";
import {User} from "../model/user.model";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {NotificationService} from "../../notification/service/notification.service";
import {Events} from "../../event/model/events.model";
import {catchError, map, takeUntil} from "rxjs/operators";
import {EventService} from "../../event/service/event.service";
import {UserUpdate} from "../model/user-update.model";

@Injectable({
    providedIn: 'root'
})
export class UserService implements OnDestroy {

    private _users: BehaviorSubject<User[]> = new BehaviorSubject<User[]>([]);
    private destroy$ = new Subject();

    constructor(private httpClient: HttpClient,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this.httpClient.get<User[]>('/api/user').toPromise()
            .then(users => this._users.next(users.map(user => new User(user))))
            .catch(reason => {
                console.error("Error while retrieving users.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving users.")
            });

        this.eventService.subscribe(Events.UPDATED_USER, User)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (user: User) => {
                    const users = this._users.getValue().slice();

                    const index = users.findIndex(current => user.id === current.id);
                    if (index >= 0) {
                        users[index] = user;
                    } else {
                        users.push(user);
                    }

                    this._users.next(users);
                }
            );

        this.eventService.subscribe(Events.DELETED_USER, User)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (user: User) => {
                    const users = this._users.getValue().slice();

                    const index = users.findIndex(current => user.id === current.id);
                    if (index >= 0) {
                        users.splice(index, 1);
                    }

                    this._users.next(users);
                }
            );
    }

    updateUser(id: string, update: UserUpdate): Observable<User> {
        return this.httpClient
            .patch('/api/user/' + id, update)
            .pipe(
                map((user: User) => new User(user)),
                catchError((result: HttpResponse<any>) => {
                        console.error('Error while updating user.', result);
                        this.notificationService.displayErrorMessage('Error while updating user.');

                        return throwError(result);
                    }
                )
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    getUsers(): Observable<User[]> {
        return this._users;
    }
}
