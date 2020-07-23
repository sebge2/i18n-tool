import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from "../../../core/auth/model/user.model";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {UserService} from "../../../core/auth/service/user.service";

@Component({
    selector: 'app-users',
    templateUrl: './users.component.html',
    styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit, OnDestroy {

    public users: User[] = [];

    private _destroyed$ = new Subject<void>();

    constructor(private _userService: UserService) {
    }

    public ngOnInit() {
        this._userService
            .getUsers()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(rep => this.users = rep);
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onSave(user: User) {

    }

    public onAdd() {

    }
}
