import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {combineLatest, Observable} from 'rxjs';
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {map} from "rxjs/operators";
import {UserRole} from "../../../auth/model/user-role.model";
import {ToolLocaleService} from "../tool-locale.service";

@Injectable({
    providedIn: 'root'
})
export class GlobalAuthGuard implements CanActivate {

    constructor(private router: Router,
                private authenticationService: AuthenticationService,
                private toolLocaleService: ToolLocaleService) {
    }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        return combineLatest([this.authenticationService.currentUser(), this.toolLocaleService.getCurrentLocale()])
            .pipe(
                map(([user, toolLocale]) => {
                        if (user == null) {
                            this.router.navigate(['/login'], {});
                            return false;
                        } else if (user.hasRole(UserRole.MEMBER_OF_ORGANIZATION)) {
                            return true;
                        } else {
                            this.router.navigate(['/error', '403'], {});
                            return false;
                        }
                    }
                )
            );
    }
}
