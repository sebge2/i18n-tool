import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {User} from "../model/user.model";
import {NotificationService} from "../../notification/service/notification.service";
import {Events} from "../../event/model/events.model";
import {catchError, map, tap} from "rxjs/operators";
import {EventService} from "../../event/service/event.service";
import {
    CurrentUserPasswordUpdateDto,
    CurrentUserPatchDto,
    InternalUserCreationDto,
    UserDto,
    UserPatchDto,
    UserService as ApiUserService
} from "../../../api";
import {ImportedFile} from "../../shared/model/imported-file.model";
import {SynchronizedCollection} from "../../shared/utils/synchronized-collection";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private _users$: Observable<User[]>;
    private _synchronizedUsers$: SynchronizedCollection<UserDto, User>;

    constructor(private apiUserService: ApiUserService,
                private eventService: EventService,
                private notificationService: NotificationService) {
    }

    public createUser(creationDto: InternalUserCreationDto): Observable<User> {
        return this.apiUserService
            .createUser(creationDto)
            .pipe(
                map(dto => User.fromDto(dto)),
                tap(user => this._synchronizedUsers$.add(user))
            );
    }

    public updateUser(id: string, update: UserPatchDto): Observable<User> {
        return this.apiUserService
            .updateUser(update, id)
            .pipe(
                map(dto => User.fromDto(dto)),
                tap(user => this._synchronizedUsers$.update(user))
            );
    }

    public updateCurrentUser(update: CurrentUserPatchDto): Observable<User> {
        this.loadUsers();

        return this.apiUserService
            .updateCurrentUser(update)
            .pipe(
                map(dto => User.fromDto(dto)),
                tap(user => this._synchronizedUsers$.update(user))
            );
    }

    public updateCurrentUserAvatar(file: ImportedFile): Observable<User> {
        return this.apiUserService
            .updateUserAvatar(file.file)
            .pipe(
                map(dto => User.fromDto(dto)),
                tap(user => this._synchronizedUsers$.update(user))
            );
    }

    public updateCurrentUserPassword(patch: CurrentUserPasswordUpdateDto): Observable<User> {
        return this.apiUserService
            .updateCurrentUserPassword(patch)
            .pipe(
                map(dto => User.fromDto(dto)),
                tap(user => this._synchronizedUsers$.update(user))
            );
    }

    public deleteUser(user: User): Observable<any> {
        this.loadUsers();

        return this.apiUserService
            ._delete(user.id)
            .pipe(tap(() => this._synchronizedUsers$.delete(user)));
    }

    public getUsers(): Observable<User[]> {
        this.loadUsers();

        return this._users$;
    }

    public getCurrentUser(): Observable<User> {
        return this.apiUserService
            .getCurrent()
            .pipe(map(user => User.fromDto(user)));
    }

    private loadUsers() {
        if (!this._synchronizedUsers$) {
            this._synchronizedUsers$ = new SynchronizedCollection<UserDto, User>(
                () => this.apiUserService.findAll(),
                this.eventService.subscribeDto(Events.ADDED_USER),
                this.eventService.subscribeDto(Events.UPDATED_USER),
                this.eventService.subscribeDto(Events.DELETED_USER),
                this.eventService.reconnected(),
                dto => User.fromDto(dto),
                (first, second) => first.id === second.id
            );

            this._users$ = this._synchronizedUsers$
                .collection
                .pipe(catchError((reason) => {
                    console.error('Error while retrieving users.', reason);
                    this.notificationService.displayErrorMessage('ADMIN.USERS.ERROR.GET_ALL');
                    return [];
                }));
        }
    }
}
