import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {flatMap, map, shareReplay, skip} from "rxjs/operators";
import {AuthenticationErrorType} from "../model/authentication-error-type.model";
import {AuthenticationService as ApiAuthenticationService, Configuration} from "../../../api";
import {EventService} from "../../event/service/event.service";
import {Events} from "../../event/model/events.model";
import {AuthenticatedUser} from '../model/authenticated-user.model';
import {OAuthClient} from "../model/oauth-client.model";
import {User} from "../model/user.model";
import {UserService} from "./user.service";
import {Router} from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    private readonly _user$: Subject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(null);
    private readonly _userObs = this._user$.pipe(skip(1), shareReplay(1));
    private readonly _currentUser$: Observable<User>;

    constructor(private httpClient: HttpClient,
                private router: Router,
                private eventService: EventService,
                private userService: UserService,
                private configuration: Configuration,
                private authenticationService: ApiAuthenticationService) {
        this.authenticationService
            .getCurrentUser()
            .pipe(map(userDto => AuthenticatedUser.fromDto(userDto)))
            .toPromise()
            .then(user => {
                console.debug('There is an existing authenticated user, send next user.', user);

                this._user$.next(user)
            })
            .catch((result: HttpResponse<any>) => {
                if (result.status != 404) {
                    console.error('Error while retrieving current user.', result);
                } else {
                    console.debug('There is no current user, send next user null.');
                }

                this._user$.next(null);
            });

        this.eventService.subscribe(Events.UPDATED_CURRENT_AUTHENTICATED_USER, AuthenticatedUser)
            .subscribe((user: AuthenticatedUser) => {
                console.debug('Current authenticated user changed.', user);
                this._user$.next(user);
            });

        this.eventService.subscribe(Events.DELETED_CURRENT_AUTHENTICATED_USER, AuthenticatedUser)
            .subscribe(_ => {
                console.debug('Current authenticated user removed.');
                this._user$.next(null);

                this.router.navigateByUrl('/login');
            });

        this.currentAuthenticatedUser()
            .subscribe(currentUser => currentUser ? eventService.enabledEvents() : eventService.disableEvents());

        this._currentUser$ = this.currentAuthenticatedUser()
            .pipe(flatMap(currentUser => currentUser ? this.userService.getCurrentUser() : null));
    }

    public currentAuthenticatedUser(): Observable<AuthenticatedUser> {
        return this._userObs;
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
            .pipe(map(userDto => AuthenticatedUser.fromDto(userDto)))
            .toPromise()
            .then(authenticatedUser => {
                this._user$.next(authenticatedUser);

                return authenticatedUser;
            })
            .catch((result: HttpResponse<any>) => {
                if (result.status == 401) {
                    throw new Error(AuthenticationErrorType.WRONG_CREDENTIALS);
                } else {
                    throw new Error(AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR);
                }
            });
    }

    public logout(): Promise<void> {
        return this.httpClient
            .get('/auth/logout')
            .toPromise()
            .then((_) => {
                console.debug('Logout succeeded, send next user.');

                this._user$.next(null);
            });
    }
}
