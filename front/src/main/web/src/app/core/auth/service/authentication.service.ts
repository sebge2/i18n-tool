import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { AuthenticationErrorType } from '../model/authentication-error-type.model';
import { AuthenticatedUserDto, AuthenticationService as ApiAuthenticationService, Configuration } from '../../../api';
import { EventService } from '@i18n-core-event';
import { Events } from '@i18n-core-event';
import { AuthenticatedUser } from '../model/authenticated-user.model';
import { OAuthClient } from '../model/oauth-client.model';
import { User } from '../model/user.model';
import { UserService } from './user.service';
import { Router } from '@angular/router';
import { SynchronizedObject } from '@i18n-core-shared';
import { NotificationService } from '@i18n-core-notification';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private readonly _user$: SynchronizedObject<AuthenticatedUserDto, AuthenticatedUser>;
  private readonly _currentUser$: Observable<User>;

  constructor(
    private _httpClient: HttpClient,
    private _router: Router,
    private _eventService: EventService,
    private _userService: UserService,
    private _configuration: Configuration,
    private _authenticationService: ApiAuthenticationService,
    private _notificationService: NotificationService
  ) {
    this._user$ = new SynchronizedObject<AuthenticatedUserDto, AuthenticatedUser>(
      () =>
        this._authenticationService.getCurrentUser().pipe(
          catchError((error: HttpResponse<any>) => {
            if (error.status != 404) {
              return throwError(() => error);
            } else {
              return of(null);
            }
          })
        ),
      this._eventService.subscribeDto(Events.UPDATED_CURRENT_AUTHENTICATED_USER),
      this._eventService.subscribeDto(Events.DELETED_CURRENT_AUTHENTICATED_USER),
      this._eventService.reconnected(),
      (dto) => AuthenticatedUser.fromDto(dto)
    );

    this.currentAuthenticatedUser().subscribe(
      (currentUser: AuthenticatedUser) => {
        if (currentUser) {
          console.debug('Current authenticated user changed.', currentUser);

          _eventService.enabledEvents();
        } else {
          console.debug('No current authenticated user.');

          this._router.navigateByUrl('/login');
          _eventService.disableEvents();
        }
      },
      (error: any) => {
        console.error('Error while retrieving current authenticated user.', error);
        this._notificationService.displayErrorMessage('USER.ERROR.GET');
      }
    );

    this._currentUser$ = this.currentAuthenticatedUser().pipe(
      mergeMap((currentUser) => (currentUser ? this._userService.getCurrentUser() : null))
    );
  }

  currentAuthenticatedUser(): Observable<AuthenticatedUser> {
    return this._user$.element;
  }

  currentUser(): Observable<User> {
    return this._currentUser$;
  }

  getSupportedOauthClients(): Observable<OAuthClient[]> {
    return this._authenticationService
      .getAuthenticationClients()
      .pipe(map((clients) => clients.map((client) => OAuthClient[client.toUpperCase()])));
  }

  authenticateWithUserPassword(username: string, password: string): Promise<AuthenticatedUser> {
    this._configuration.username = username;
    this._configuration.password = password;

    return this._authenticationService
      .getCurrentUser()
      .pipe(map((userDto) => AuthenticatedUser.fromDto(userDto)))
      .toPromise()
      .then((authenticatedUser) => {
        this._user$.update(authenticatedUser);

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

  logout(): Promise<void> {
    return this._httpClient
      .get('/auth/logout')
      .toPromise()
      .then((_) => {
        console.debug('Logout succeeded, send next user.');

        this._user$.delete();
      });
  }
}
