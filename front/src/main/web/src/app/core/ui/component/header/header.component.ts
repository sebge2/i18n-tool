import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {MatSidenav} from '@angular/material/sidenav';
import {ScreenService} from '../../service/screen.service';
import {User} from '@i18n-core-auth';
import {AuthenticationService} from '@i18n-core-auth';
import {takeUntil} from 'rxjs/operators';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit, OnDestroy {
    @Output() toggle = new EventEmitter<void>();

    private readonly _destroyed$ = new Subject<void>();
    private _currentUser: User = null;
    private _smallSize: boolean;

    constructor(private authService: AuthenticationService, private mediaService: ScreenService) {
    }

    ngOnInit() {
        this.mediaService.smallSize
            .pipe(takeUntil(this._destroyed$))
            .subscribe((smallSize) => (this._smallSize = smallSize));

        this.authService
            .currentUser()
            .pipe(takeUntil(this._destroyed$))
            .subscribe((user) => {
                this._currentUser = user != null ? user : null;
            });
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    get currentUser(): User {
        return this._currentUser;
    }

    get smallSize(): Observable<boolean> {
        return this.mediaService.smallSize;
    }

    get avatarUrl(): string {
        return this.currentUser != null ? `/api/user/${this.currentUser.id}/avatar` : null;
    }

    get userDisplayName(): string {
        return this.currentUser != null ? this.currentUser.displayName : null;
    }

    onToggle(): void {
        this.toggle.emit();
    }
}
