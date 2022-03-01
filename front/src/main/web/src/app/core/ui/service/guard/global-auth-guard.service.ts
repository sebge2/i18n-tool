import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {AuthenticationService, UserRole} from '@i18n-core-auth';
import {mergeMap} from 'rxjs/operators';
import {ToolLocaleService} from '@i18n-core-translation';
import {RedirectService} from "../../../auth/service/redirect.service";

@Injectable({
    providedIn: 'root',
})
export class GlobalAuthGuard implements CanActivate {

    constructor(
        private _router: Router,
        private _redirectService: RedirectService,
        private _authenticationService: AuthenticationService,
        private _toolLocaleService: ToolLocaleService
    ) {
    }

    canActivate(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): Observable<boolean> {
        return combineLatest([this._authenticationService.currentAuthenticatedUser(), this._toolLocaleService.getCurrentLocale()])
            .pipe(
                mergeMap(([user, _]) => {
                    if (user == null) {
                        return this._redirectService.redirectToLogin();
                    } else if (user.hasRole(UserRole.MEMBER_OF_ORGANIZATION)) {
                        return of(true);
                    } else {
                        return this._redirectService.redirectToUnauthorized();
                    }
                })
            );
    }
}
