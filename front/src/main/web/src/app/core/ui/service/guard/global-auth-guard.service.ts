import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {HttpErrorResponse} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class GlobalAuthGuard implements CanActivate {

    constructor(private router: Router,
                private authenticationService: AuthenticationService) {
    }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        return this.authenticationService.currentUser
            .toPromise()
            .then(user => {
                if (user.roles.includes("ROLE_USER") && user.roles.includes("REPO_MEMBER")) {
                    return true;
                } else {
                    return this.router.navigate(['/error', '403'], {});
                }
            })
            .catch((reason: HttpErrorResponse) => {
                if (reason.status == 404) {
                    window.location.href = '/login';
                }else {
                    console.error("Error while retrieving current user.", reason);
                }

                return false;
            });
    }
}
