import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';
import { NotificationService } from '@i18n-core-notification';
import { Events } from '@i18n-core-event';
import { catchError, map, tap } from 'rxjs/operators';
import { EventService } from '@i18n-core-event';
import {
  CurrentUserPasswordUpdateDto,
  CurrentUserPatchDto,
  InternalUserCreationDto,
  UserDto,
  UserPatchDto,
  UserService as ApiUserService,
} from '../../../api';
import { ImportedFile } from '@i18n-core-shared';
import { SynchronizedCollection } from '@i18n-core-shared';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private _users$: Observable<User[]>;
  private _synchronizedUsers$: SynchronizedCollection<UserDto, User>;

  constructor(
    private _apiUserService: ApiUserService,
    private _eventService: EventService,
    private _notificationService: NotificationService
  ) {}

  createUser(creationDto: InternalUserCreationDto): Observable<User> {
    return this._apiUserService.createUser(creationDto).pipe(
      map((dto) => User.fromDto(dto)),
      tap((user) => this._synchronizedUsers$.add(user))
    );
  }

  updateUser(id: string, update: UserPatchDto): Observable<User> {
    return this._apiUserService.updateUser(update, id).pipe(
      map((dto) => User.fromDto(dto)),
      tap((user) => this._synchronizedUsers$.update(user))
    );
  }

  updateCurrentUser(update: CurrentUserPatchDto): Observable<User> {
    this._loadUsers();

    return this._apiUserService.updateCurrentUser(update).pipe(
      map((dto) => User.fromDto(dto)),
      tap((user) => this._synchronizedUsers$.update(user))
    );
  }

  updateCurrentUserAvatar(file: ImportedFile): Observable<User> {
    return this._apiUserService.updateUserAvatar(file.file).pipe(
      map((dto) => User.fromDto(dto)),
      tap((user) => this._synchronizedUsers$.update(user))
    );
  }

  updateCurrentUserPassword(patch: CurrentUserPasswordUpdateDto): Observable<User> {
    return this._apiUserService.updateCurrentUserPassword(patch).pipe(
      map((dto) => User.fromDto(dto)),
      tap((user) => this._synchronizedUsers$.update(user))
    );
  }

  deleteUser(user: User): Observable<any> {
    this._loadUsers();

    return this._apiUserService._delete(user.id).pipe(tap(() => this._synchronizedUsers$.delete(user)));
  }

  getUsers(): Observable<User[]> {
    this._loadUsers();

    return this._users$;
  }

  getCurrentUser(): Observable<User> {
    return this._apiUserService.getCurrent().pipe(map((user) => User.fromDto(user)));
  }

  private _loadUsers() {
    if (!this._synchronizedUsers$) {
      this._synchronizedUsers$ = new SynchronizedCollection<UserDto, User>(
        () => this._apiUserService.findAll(),
        this._eventService.subscribeDto(Events.ADDED_USER),
        this._eventService.subscribeDto(Events.UPDATED_USER),
        this._eventService.subscribeDto(Events.DELETED_USER),
        this._eventService.reconnected(),
        (dto) => User.fromDto(dto),
        (first, second) => first.id === second.id
      );

      this._users$ = this._synchronizedUsers$.collection.pipe(
        catchError((reason) => {
          console.error('Error while retrieving users.', reason);
          this._notificationService.displayErrorMessage('ADMIN.USERS.ERROR.GET_ALL');
          return [];
        })
      );
    }
  }
}
