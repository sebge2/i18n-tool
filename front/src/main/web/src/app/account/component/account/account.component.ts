import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {AuthenticationService} from "../../../core/auth/service/authentication.service";
import {User} from "../../../core/auth/model/user.model";

@Component({
    selector: 'app-account',
    templateUrl: './account.component.html',
    styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit, OnDestroy {

    public currentUser: User;

    private readonly _destroyed$ = new Subject();

    constructor(private authenticationService: AuthenticationService) {
    }

    public ngOnInit() {
        this.authenticationService.currentUser()
            .subscribe(currentUser => this.currentUser = currentUser);
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }
}
