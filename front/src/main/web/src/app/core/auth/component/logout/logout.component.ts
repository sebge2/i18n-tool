import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from '../../service/authentication.service';
import {NotificationService} from '@i18n-core-notification';
import {catchError, takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";

@Component({
    selector: 'app-logout',
    templateUrl: './logout.component.html',
    styleUrls: ['./logout.component.css'],
})
export class LogoutComponent implements OnInit, OnDestroy {
    loggedOut: boolean = null;

    private readonly _destroyed$ = new Subject<void>();

    constructor(
        private _authenticationService: AuthenticationService,
        private _notificationService: NotificationService
    ) {
    }

    ngOnInit() {
        this._authenticationService
            .logout()
            .pipe(
                takeUntil(this._destroyed$),
                catchError((error: any) => {
                    console.error('Error while login out user.', error);
                    this._notificationService.displayErrorMessage('Error while login out user.');

                    this.loggedOut = false;

                    return null;
                }),
            )
            .subscribe((_) => this.loggedOut = true);
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }
}
