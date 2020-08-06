import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {User} from "../model/user.model";
import {NotificationService} from "../../notification/service/notification.service";
import {Events} from "../../event/model/events.model";
import {catchError, map} from "rxjs/operators";
import {EventService} from "../../event/service/event.service";
import {
    CurrentUserPasswordUpdateDto,
    CurrentUserPatchDto,
    InternalUserCreationDto,
    UserPatchDto,
    UserService as ApiUserService
} from "../../../api";
import {synchronizedCollection} from "../../shared/utils/synchronized-observable-utils";
import {ImportedFile} from "../../shared/model/imported-file.model";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private _users: Observable<User[]>;

    constructor(private apiUserService: ApiUserService,
                private eventService: EventService,
                private notificationService: NotificationService) {
    }

    public createUser(creationDto: InternalUserCreationDto): Observable<User> {
        return this.apiUserService
            .createUser(creationDto)
            .pipe(map(dto => User.fromDto(dto)));
    }

    public updateUser(id: string, update: UserPatchDto): Observable<User> {
        return this.apiUserService
            .updateUser(update, id)
            .pipe(map(dto => User.fromDto(dto)));
    }

    public updateCurrentUser(update: CurrentUserPatchDto): Observable<User> {
        return this.apiUserService
            .updateCurrentUser(update)
            .pipe(map(dto => User.fromDto(dto)));
    }

    public updateCurrentUserAvatar(file: ImportedFile): Observable<any> {
        return this.apiUserService
            .updateUserAvatar(file.file);
    }

    public updateCurrentUserPassword(patch: CurrentUserPasswordUpdateDto): Observable<User> {
        return this.apiUserService
            .updateCurrentUserPassword(patch)
            .pipe(map(dto => User.fromDto(dto)));
    }

    public deleteUser(id: string): Observable<any> {
        return this.apiUserService.deleteUserById(id);
    }

    public getUsers(): Observable<User[]> {
        if(!this._users){
            this._users = synchronizedCollection(
                this.apiUserService.findAll2(),
                this.eventService.subscribeDto(Events.ADDED_USER),
                this.eventService.subscribeDto(Events.UPDATED_USER),
                this.eventService.subscribeDto(Events.DELETED_USER),
                dto => User.fromDto(dto),
                (first, second) => first.id === second.id
            )
                .pipe(catchError((reason) => {
                    console.error("Error while retrieving users.", reason);
                    this.notificationService.displayErrorMessage("Error while retrieving users.");
                    return [];
                }));
        }

        return this._users;
    }

    public getCurrentUser(): Observable<User> {
        return this.apiUserService
            .getCurrent()
            .pipe(map(user => User.fromDto(user)));
    }
}
