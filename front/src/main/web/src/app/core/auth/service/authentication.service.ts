import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable, of, Subject} from "rxjs";
import {User} from "../model/user.model";
import {catchError, map, tap} from "rxjs/operators";
import {NotificationService} from "../../notification/service/notification.service";
import {AuthenticationErrorType} from "../model/authentication-error-type.model";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService implements OnDestroy {

    private _user: Subject<User> = new Subject<User>();
    private destroy$ = new Subject();

    constructor(private httpClient: HttpClient,
                private notificationService: NotificationService) {
        this.httpClient.get<User>('/api/authentication/user')
            .pipe(map(user => new User(user)))
            .subscribe(
                user => {
                    console.debug('There is a current user, send next user.', user);
                    this._user.next(user)
                },
                (result: HttpResponse<any>) => {
                    if (result.status != 404) {
                        console.error('Error while retrieving current user.', result);
                        this.notificationService.displayErrorMessage("Error while retrieving current user.");
                    } else {
                        console.debug('There is no current user, send next user null.');
                    }

                    this._user.next(null);
                }
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    get currentUser(): Observable<User> {
        return this._user;
    }

    authenticateWithUserPassword(username: string, password: string): Observable<User> {
        return this.httpClient
            .get(
                "/api/authentication/user",
                {
                    headers: {
                        'Authorization': 'Basic ' + btoa(username + ':' + password),
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                }
            )
            .pipe(
                map(
                    (user: User) => {
                        return new User(user);
                    }
                ),
                catchError((result: HttpResponse<any>) => {
                        if (result.status == 401) {
                            return of(AuthenticationErrorType.WRONG_CREDENTIALS);
                        } else {
                            console.error('Error while authenticating user.', result);
                            this.notificationService.displayErrorMessage('Error while authenticating user.');

                            return of(AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR);
                        }
                    }
                ),
                tap(
                    (value: any) => {
                        if (value instanceof User) {
                            console.log('Authentication succeeded, send next user.', value);

                            this._user.next(<User>value);
                        }
                    }
                )
            );
    }

    authenticateWithGitHubAuthKey(authKey: string): Observable<User> {
        return this.httpClient
            .get(
                "/api/authentication/user",
                {
                    headers: {
                        'Authorization': 'Basic ' + btoa('#' + authKey + ':'),
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                }
            )
            .pipe(
                map(
                    (user: User) => {
                        return new User(user);
                    }
                ),
                catchError((result: HttpResponse<any>) => {
                        if (result.status == 401) {
                            return of(AuthenticationErrorType.WRONG_CREDENTIALS);
                        } else {
                            console.error('Error while authenticating user.', result);
                            this.notificationService.displayErrorMessage('Error while authenticating user.');

                            return of(AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR);
                        }
                    }
                ),
                tap(
                    (value: any) => {
                        if (value instanceof User) {
                            console.log('Authentication succeeded, send next user.', value);

                            this._user.next(<User>value);
                        }
                    }
                )
            );
    }
}