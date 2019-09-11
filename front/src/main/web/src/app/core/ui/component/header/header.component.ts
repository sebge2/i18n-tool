import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {MatSidenav} from '@angular/material';
import {ScreenService} from '../../service/screen.service';
import {User} from "../../../auth/model/user.model";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {UserSessionService} from "../../../auth/service/user-session.service";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

    @Input() sideBar: MatSidenav;

    private _currentUser: User = null;
    private currentUserSubscription: Subscription;

    private _smallSizeSubscription: Subscription;
    private _smallSize: boolean;

    private _userSessionsSubscription: Subscription;

    constructor(private authService: AuthenticationService,
                private mediaService: ScreenService,
                private userSessionService: UserSessionService) {
        this._smallSizeSubscription = mediaService.smallSize
            .subscribe(
                smallSize => this._smallSize = smallSize
            );
    }

    ngOnInit() {
        this.currentUserSubscription = this.authService.currentUser.subscribe(
            user => this._currentUser = user
        );

        // this._userSessionsSubscription = this.userSessionService.getCurrentUserSessions().subscribe(
        //     userSessions => console.log("updated sessions", userSessions) TODO
        // );
    }

    ngOnDestroy(): void {
        this.currentUserSubscription.unsubscribe();
        this._smallSizeSubscription.unsubscribe();
        // this._userSessionsSubscription.unsubscribe();
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
