import { Component, OnDestroy, OnInit } from '@angular/core';
import { User } from '@i18n-core-auth';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UserService } from '@i18n-core-auth';
import * as _ from 'lodash';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
})
export class UsersComponent implements OnInit, OnDestroy {
  users: User[] = [];

  private readonly _addedUsers = new BehaviorSubject<User[]>([]);
  private readonly _destroyed$ = new Subject<void>();

  constructor(private _userService: UserService) {}

  ngOnInit() {
    combineLatest([this._userService.getUsers(), this._addedUsers])
      .pipe(takeUntil(this._destroyed$))
      .subscribe(([availableUsers, addedUsers]) => {
        this.users = [];
        this.users = _.concat(this.users, availableUsers);
        this.users = _.concat(this.users, addedUsers);
      });
  }

  ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  onAdd() {
    const users = this._addedUsers.getValue();
    users.push(User.createInternalUser());

    this._addedUsers.next(users);
  }

  onSave(user: User) {
    this._removeFromAddedUsers(user);
  }

  onDelete(user: User) {
    this._removeFromAddedUsers(user);
  }

  private _removeFromAddedUsers(user: User) {
    const users = this._addedUsers.getValue();
    const indexOf = users.indexOf(user);

    if (indexOf >= 0) {
      users.splice(indexOf, 1);
      this._addedUsers.next(users);
    }
  }
}
