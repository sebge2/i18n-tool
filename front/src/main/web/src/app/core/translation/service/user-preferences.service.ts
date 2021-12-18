import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { UserPreferencesDto, UserPreferencesService as ApiUserPreferencesService } from '../../../api';
import { Events } from '@i18n-core-event';
import { catchError, map, mergeMap, tap } from 'rxjs/operators';
import { EventService } from '@i18n-core-event';
import { NotificationService } from '@i18n-core-notification';
import { AuthenticationService } from '@i18n-core-auth';
import { UserPreferences } from '../model/user-preferences';
import { SynchronizedObject } from '@i18n-core-shared';

@Injectable({
  providedIn: 'root',
})
export class UserPreferencesService {
  private readonly _synchronizedUserPreferences$: SynchronizedObject<UserPreferencesDto, UserPreferences>;
  private readonly _userPreferences$: Observable<UserPreferences>;

  constructor(
    private apiService: ApiUserPreferencesService,
    private authService: AuthenticationService,
    private eventService: EventService,
    private notificationService: NotificationService
  ) {
    this._synchronizedUserPreferences$ = new SynchronizedObject<UserPreferencesDto, UserPreferences>(
      () =>
        this.authService
          .currentAuthenticatedUser()
          .pipe(mergeMap((currentUser) => (currentUser ? this.apiService.getCurrentUserPreferences() : of(null)))),
      this.eventService.subscribeDto(Events.UPDATED_USER_PREFERENCES),
      of(),
      this.eventService.reconnected(),
      (dto) => (dto != null ? UserPreferences.fromDto(dto) : null)
    );

    this._userPreferences$ = this._synchronizedUserPreferences$.element.pipe(
      catchError((reason) => {
        console.error('Error while retrieving user preferences.', reason);
        this.notificationService.displayErrorMessage('PREFERENCES.ERROR.GET');
        return [];
      })
    );
  }

  public getUserPreferences(): Observable<UserPreferences> {
    return this._userPreferences$;
  }

  public updateUserPreferences(userPreferences: UserPreferences): Observable<UserPreferences> {
    return this.apiService.updateCurrentUserPreferences(userPreferences.toDto()).pipe(
      map((userPreferences) => UserPreferences.fromDto(userPreferences)),
      tap((userPreferences) => this._synchronizedUserPreferences$.update(userPreferences))
    );
  }
}
