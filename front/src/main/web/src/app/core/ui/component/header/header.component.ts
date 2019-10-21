import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject} from 'rxjs';
import {MatSidenav} from '@angular/material';
import {ScreenService} from '../../service/screen.service';
import {User} from "../../../auth/model/user.model";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

    @Input() sideBar: MatSidenav;

    private _currentUser: User = null;
    private destroy$ = new Subject();

    private _smallSize: boolean;

    constructor(private authService: AuthenticationService,
                private mediaService: ScreenService) {
    }

    ngOnInit() {
        this.mediaService.smallSize
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                smallSize => this._smallSize = smallSize
            );

        this.authService.currentUser
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                user => {
                    this._currentUser = user;
                }
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    get currentUser(): User {
        return this._currentUser;
    }

    getUrl(): string {
        return (this.currentUser != null)
            ? this.currentUser.avatarUrl
            : null;
    }

    getDisplayName(): string {
        return (this.currentUser) != null ? this.currentUser.username : null;
    }

    get smallSize(): boolean {
        return this._smallSize;
    }
}
