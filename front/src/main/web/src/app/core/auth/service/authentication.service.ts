import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {BehaviorSubject, Observable, Subject, throwError} from "rxjs";
import {User} from "../model/user.model";
import {catchError, map, skipWhile, tap} from "rxjs/operators";
import {NotificationService} from "../../notification/service/notification.service";
import {AuthenticationErrorType} from "../model/authentication-error-type.model";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService implements OnDestroy {

    private _user: Subject<User> = new BehaviorSubject<User>(null);
    private initialized: boolean = false;
    private destroy$ = new Subject();

    constructor(private httpClient: HttpClient,
                private notificationService: NotificationService) {
        this.httpClient.get<User>('/api/authentication/user')
            .pipe(map(user => new User(user)))
            .toPromise()
            .then(
                user => {
                    console.debug('There is an existing authenticated user, send next user.', user);

                    this.initialized = true;
                    this._user.next(user)
                }
            )
            .catch(
                (result: HttpResponse<any>) => {
                    if (result.status != 404) {
                        console.error('Error while retrieving current user.', result);
                        this.notificationService.displayErrorMessage("Error while retrieving current user.");
                    } else {
                        console.debug('There is no current user, send next user null.');
                    }

                    this.initialized = true;
                    this._user.next(null);
                }
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    get currentUser(): Observable<User> {
        return this._user.pipe(skipWhile(val => !this.initialized));
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
                            return throwError(AuthenticationErrorType.WRONG_CREDENTIALS);
                        } else {
                            console.error('Error while authenticating user.', result);
                            this.notificationService.displayErrorMessage('Error while authenticating user.');

                            return throwError(AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR);
                        }
                    }
                ),
                tap(
                    (value: any) => {
                        if (value instanceof User) {
                            console.debug('Authentication succeeded, send next user.', value);

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
                            return throwError(AuthenticationErrorType.WRONG_CREDENTIALS);
                        } else {
                            console.error('Error while authenticating user.', result);
                            this.notificationService.displayErrorMessage('Error while authenticating user.');

                            return throwError(AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR);
                        }
                    }
                ),
                tap(
                    (value: any) => {
                        if (value instanceof User) {
                            console.debug('Authentication succeeded, send next user.', value);

                            this._user.next(<User>value);
                        }
                    }
                ),
                tap(
                    (value: any) => {
                        if (value instanceof User) {
                            console.debug('Authentication succeeded, send next user.', value);

                            this._user.next(<User>value);
                        }
                    }
                )
            );
    }

    logout(): Observable<any> {
        return this.httpClient
            .get('/auth/logout')
            .pipe(
                map(
                    (result: any) => {
                        return null;
                    }
                ),
                catchError((result: HttpResponse<any>) => {
                        console.error('Error while login out user.', result);
                        this.notificationService.displayErrorMessage('Error while login out user.');

                        return throwError(AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR);
                    }
                ),
                tap(
                    (value: any) => {
                        if (value == null) {
                            console.debug('Logout succeeded, send next user.', value);

                            this._user.next(null);
                        }
                    }
                )
            );
    }
}