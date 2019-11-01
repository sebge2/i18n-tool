import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from "../../../core/auth/service/user.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {User} from "../../../core/auth/model/user.model";
import {MatTableDataSource} from "@angular/material";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
    selector: 'app-user-table',
    templateUrl: './user-table.component.html',
    styleUrls: ['./user-table.component.css'],
    animations: [
        trigger('detailExpand', [
            state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
            state('expanded', style({height: '*'})),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ])
    ]
})
export class UserTableComponent implements OnInit, OnDestroy {

    displayedColumns = ['name', 'info', 'action'];
    hoveredUser: User;
    dataSource = new MatTableDataSource<User>([]);

    expandedUser: User = null;
    actionInProgress: boolean = false;

    private destroy$ = new Subject();

    constructor(private userService: UserService) {
    }

    ngOnInit() {
        this.userService.getUsers()
            .pipe(takeUntil(this.destroy$))
            .subscribe((users: User[]) => {
                this.dataSource.data = users;
            });
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    edit(user: User) {
    }

    delete(user: User) {
    }
}
