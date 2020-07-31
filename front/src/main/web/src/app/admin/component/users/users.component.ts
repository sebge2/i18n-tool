import {Component, OnDestroy, OnInit} from '@angular/core';
import {User, UserType} from "../../../core/auth/model/user.model";
import {BehaviorSubject, combineLatest, Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {UserService} from "../../../core/auth/service/user.service";
import * as _ from "lodash";

@Component({
    selector: 'app-users',
    templateUrl: './users.component.html',
    styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit, OnDestroy {

    public users: User[] = [];

    private readonly _addedUsers = new BehaviorSubject<User[]>([]);
    private _destroyed$ = new Subject<void>();

    constructor(private _userService: UserService) {
    }

    public ngOnInit() {
        combineLatest([this._userService.getUsers(), this._addedUsers])
            .pipe(takeUntil(this._destroyed$))
            .subscribe(([availableUsers, addedUsers]) => {
                this.users = [];
                this.users = _.concat(this.users, availableUsers);
                this.users = _.concat(this.users, addedUsers);
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onAdd() {
        const locales = this._addedUsers.getValue();
        locales.push(User.createInternalUser());

        this._addedUsers.next(locales);
    }

    public onSave(user: User) {
        this.removeFromAddedUsers(user);
    }

    public onDelete(user: User) {
        this.removeFromAddedUsers(user);
    }

    private removeFromAddedUsers(user: User) {
        const locales = this._addedUsers.getValue();
        const indexOf = locales.indexOf(user);

        if (indexOf >= 0) {
            locales.splice(indexOf, 1);
            this._addedUsers.next(locales);
        }
    }
}
