import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable, ReplaySubject, Subject} from "rxjs";
import {flatMap, map} from "rxjs/operators";
import {NotificationService} from "../../notification/service/notification.service";
import {AuthenticationErrorType} from "../model/authentication-error-type.model";
import {AuthenticationService as ApiAuthenticationService, Configuration} from "../../../api";
import {EventService} from "../../event/service/event.service";
import {Events} from "../../event/model/events.model";
import {AuthenticatedUser} from '../model/authenticated-user.model';
import {OAuthClient} from "../model/oauth-client.model";
import {User} from "../model/user.model";
import {UserService} from "./user.service";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    private readonly _user$: Subject<AuthenticatedUser> = new ReplaySubject<AuthenticatedUser>();
    private readonly _currentUser$: Observable<User>;

    constructor(private httpClient: HttpClient,
                private eventService: EventService,
                private userService: UserService,
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
            .subscribe(
                (user: AuthenticatedUser) => {
                    console.debug('Current authenticated user changed.', user);
                    this._user$.next(user);
                }
            );

        this._currentUser$ = this.currentAuthenticatedUser()
            .pipe(flatMap(currentUser => this.userService.getUserById(currentUser.user.id)));
    }

    public currentAuthenticatedUser(): Observable<AuthenticatedUser> {
        return this._user$;
    }

    public currentUser(): Observable<User> {
        return this._currentUser$;
    }

    public getSupportedOauthClients(): Observable<OAuthClient[]> {
        return this.authenticationService
            .getAuthenticationClients()
            .pipe(map(clients => clients.map(client => OAuthClient[client.toUpperCase()])));
    }

    public authenticateWithUserPassword(username: string, password: string): Promise<AuthenticatedUser> {
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

    public logout(): Promise<void> {
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
