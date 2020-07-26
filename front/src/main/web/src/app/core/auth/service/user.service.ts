import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {User} from "../model/user.model";
import {NotificationService} from "../../notification/service/notification.service";
import {Events} from "../../event/model/events.model";
import {catchError, map} from "rxjs/operators";
import {EventService} from "../../event/service/event.service";
import {UserUpdate} from "../model/user-update.model";
import {InternalUserCreationDto, UserService as ApiUserService} from "../../../api";
import {synchronizedCollection} from "../../shared/utils/synchronized-observable-utils";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private readonly _users: Observable<User[]>;

    constructor(private apiUserService: ApiUserService,
                private eventService: EventService,
                private notificationService: NotificationService) {
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

    public createUser(creationDto: InternalUserCreationDto): Observable<User> {
        return this.apiUserService
            .createUser(creationDto)
            .pipe(map(dto => User.fromDto(dto)));
    }

    // TODO
    public updateUser(id: string, update: UserUpdate): Observable<User> {
        return null;
        // return this.apiUserService
        //     .updateUser()
        //     .patch('/api/user/' + id, update)
        //     .pipe(
        //         map((user: User) => new User(user)),
        //         catchError((result: HttpResponse<any>) => {
        //                 console.error('Error while updating user.', result);
        //                 this.notificationService.displayErrorMessage('Error while updating user.');
        //
        //                 return throwError(result);
        //             }
        //         )
        //     );
    }

    public deleteUser(id: string): Observable<any> {
        return this.apiUserService.deleteUserById(id);
    }

    public getUsers(): Observable<User[]> {
        return this._users;
    }
}
