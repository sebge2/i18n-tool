import {Injectable} from '@angular/core';
import {from, Observable} from "rxjs";
import {Router} from "@angular/router";
import * as _ from "lodash";
import {LOGIN_PATH_SEGMENT, LOGOUT_PATH_SEGMENT} from '../model/auth-constant.model';

@Injectable({
    providedIn: 'root'
})
export class RedirectService {

    static readonly LOGIN_PATH = `${LOGIN_PATH_SEGMENT}`;
    static readonly LOGOUT_PATH = `${LOGOUT_PATH_SEGMENT}`;
    static readonly DEFAULT_PATH_AFTER_LOGIN = '/translations';
    static readonly ORIGIN_PARAM = 'origin';

    constructor(private _router: Router) {
    }

    redirectToLogin(): Observable<boolean> {
        return from(this._router.navigate(
            [RedirectService.LOGIN_PATH],
            {
                queryParams: {[RedirectService.ORIGIN_PARAM]: RedirectService._computeCurrentOriginParameter()}
            }
        ));
    }

    redirectToUnauthorized(): Observable<boolean> {
        return from(this._router.navigate(['/error', '403'], {}));
    }

    redirectAfterLogin(): Observable<boolean> {
        const origin = RedirectService._getCurrentOriginParameter();

        if (_.isEmpty(origin)) {
            return from(this._router.navigate([RedirectService.DEFAULT_PATH_AFTER_LOGIN]));
        } else {
            return from(this._router.navigateByUrl(origin));
        }
    }

    /**
     * Returns the value of the origin parameter allowing to go back to the page after login. We don't go back
     * to logout after login otherwise we loop forever.
     */
    private static _computeCurrentOriginParameter() {
        if (window.location.pathname === RedirectService.LOGOUT_PATH) {
            return null;
        }

        return `${window.location.pathname}${window.location.search}`;
    }

    private static _getCurrentOriginParameter() {
        return new URL(window.location.href).searchParams.get(RedirectService.ORIGIN_PARAM);
    }
}
