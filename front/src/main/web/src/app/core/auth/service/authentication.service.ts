import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable, Subject} from "rxjs";
import {map, takeUntil} from "rxjs/operators";
import {NotificationService} from "../../notification/service/notification.service";
import {AuthenticationErrorType} from "../model/authentication-error-type.model";
import {AuthenticationService as ApiAuthenticationService, Configuration} from "../../../api";
import {EventService} from "../../event/service/event.service";
import {Events} from "../../event/model/events.model";
import {AuthenticatedUser} from '../model/authenticated-user.model';
import {OAuthClient} from "../model/oauth-client.model";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService implements OnDestroy {

    private _user$: Subject<AuthenticatedUser> = new Subject<AuthenticatedUser>();
    private _destroyed$ = new Subject();

    constructor(private httpClient: HttpClient,
                private eventService: EventService,
                private notificationService: NotificationService,
                private configuration: Configuration,
                private authenticationService: ApiAuthenticationService) {
        this.authenticationService
            .getCurrentUser()
            .pipe(map(userDto => new AuthenticatedUser(userDto)))
            .toPromise()
            .then(user => {
                console.debug('There is an existing authenticated user, send next user.', user);

                this._user$.next(user)
            })
            .catch((result: HttpResponse<any>) => {
                if (result.status != 404) {
                    console.error('Error while retrieving current user.', result);
                    this.notificationService.displayErrorMessage("Error while retrieving current user.");
                } else {
                    console.debug('There is no current user, send next user null.');
                }

                this._user$.next(null);
            });

        this.eventService.subscribe(Events.UPDATED_CURRENT_AUTHENTICATED_USER, AuthenticatedUser)
            .pipe(takeUntil(this._destroyed$))
            .subscribe(
                (user: AuthenticatedUser) => {
                    console.debug('Current authenticated user changed.', user);
                    this._user$.next(user);
                }
            );
    }

    ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    currentUser(): Observable<AuthenticatedUser> {
        return this._user$;
    }

    getSupportedOauthClients(): Observable<OAuthClient[]> {
        return this.authenticationService
            .getAuthenticationClients()
            .pipe(map(clients => clients.map(client => OAuthClient[client.toUpperCase()])));
    }

    authenticateWithUserPassword(username: string, password: string): Promise<AuthenticatedUser> {
        this.configuration.username = username;
        this.configuration.password = password;

        return this.authenticationService
            .getCurrentUser()
            .pipe(map(userDto => new AuthenticatedUser(userDto)))
            .toPromise()
            .then(authenticatedUser => {
                this._user$.next(authenticatedUser);

                return authenticatedUser;
            })
            .catch((result: HttpResponse<any>) => {
                if (result.status == 401) {
                    throw new Error(AuthenticationErrorType.WRONG_CREDENTIALS);
                } else {
                    console.error('Error while authenticating user.', result);
                    this.notificationService.displayErrorMessage('Error while authenticating user.');

                    throw new Error(AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR);
                }
            });
    }

    logout(): Promise<void> {
        return this.httpClient
            .get('/auth/logout')
            .toPromise()
            .then((_) => {
                console.debug('Logout succeeded, send next user.', null);

                this._user$.next(null);
            })
            .catch((result: HttpResponse<any>) => {
                console.error('Error while login out user.', result);
                this.notificationService.displayErrorMessage('Error while login out user.');

                throw new Error(AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR);
            });
    }
}
