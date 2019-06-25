import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {BehaviorSubject, Observable, Subject, throwError} from "rxjs";
import {catchError, map, skipWhile, takeUntil, tap} from "rxjs/operators";
import {NotificationService} from "../../notification/service/notification.service";
import {AuthenticationErrorType} from "../model/authentication-error-type.model";
import {EventService} from "../../event/service/event.service";
import {Events} from "../../event/model/events.model";
import {AuthenticatedUser} from '../model/authenticated-user.model';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService implements OnDestroy {

    private _user: Subject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(null);
    private initialized: boolean = false;
    private destroy$ = new Subject();

    constructor(private httpClient: HttpClient,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this.httpClient.get<AuthenticatedUser>('/api/authentication/user')
            .pipe(map(user => new AuthenticatedUser(user)))
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

        this.eventService.subscribe(Events.UPDATED_CURRENT_AUTHENTICATED_USER, AuthenticatedUser)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (user: AuthenticatedUser) => {
                    console.debug('Current authenticated user changed.', user);
                    this._user.next(user);
                }
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    currentUser(): Observable<AuthenticatedUser> {
        return this._user.pipe(skipWhile(val => !this.initialized));
    }

    authenticateWithUserPassword(username: string, password: string): Observable<AuthenticatedUser> {
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
                    (user: AuthenticatedUser) => {
                        return new AuthenticatedUser(user);
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
                        if (value instanceof AuthenticatedUser) {
                            console.debug('Authentication succeeded, send next user.', value);

                            this._user.next(<AuthenticatedUser>value);
                        }
                    }
                )
            );
    }

    authenticateWithGitHubAuthKey(authKey: string): Observable<AuthenticatedUser> {
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
                    (user: AuthenticatedUser) => {
                        return new AuthenticatedUser(user);
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
                        if (value instanceof AuthenticatedUser) {
                            console.debug('Authentication succeeded, send next user.', value);

                            this._user.next(<AuthenticatedUser>value);
                        }
                    }
                ),
                tap(
                    (value: any) => {
                        if (value instanceof AuthenticatedUser) {
                            console.debug('Authentication succeeded, send next user.', value);

                            this._user.next(<AuthenticatedUser>value);
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