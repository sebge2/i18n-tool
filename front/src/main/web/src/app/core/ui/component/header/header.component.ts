import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {MatSidenav} from '@angular/material';
import {ScreenService} from '../../service/screen.service';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

    @Input() sideBar: MatSidenav;

    // private _currentUser: User = null;
    // private currentUserSubscription: Subscription;

    private _smallSizeSubscription: Subscription;
    private _smallSize: boolean;

    constructor(/*private authService: AuthService,*/
                mediaService: ScreenService) {
        this._smallSizeSubscription = mediaService.smallSize
            .subscribe(
                smallSize => this._smallSize = smallSize
            );
    }

    ngOnInit() {
        // this.currentUserSubscription = this.authService.currentUser.subscribe(
        //   user => this._currentUser = user
        // );
    }

    ngOnDestroy(): void {
        // this.currentUserSubscription.unsubscribe();
        this._smallSizeSubscription.unsubscribe();
    }

    // get currentUser(): User {
    //   return this._currentUser;
    // }

    // getUrl(): string {
    //     return (this.currentUser != null)
    //         ? this.currentUser.photoUrl
    //         : null;
    // }

    // getDisplayName(): string {
    //     return (this.currentUser) != null ? this.currentUser.displayName : null;
    // }

    get smallSize(): boolean {
        return this._smallSize;
    }
}
