import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {HttpErrorResponse} from "@angular/common/http";
import {NotificationService} from "../../../notification/service/notification.service";

@Injectable({
    providedIn: 'root'
})
export class GlobalAuthGuard implements CanActivate {

    constructor(private router: Router,
                private authenticationService: AuthenticationService,
                private notificationService: NotificationService) {
    }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        return this.authenticationService.currentUser
            .toPromise()
            .then(user => {
                if (user.roles.includes("MEMBER_OF_ORGANIZATION")) {
                    return true;
                } else {
                    return this.router.navigate(['/error', '403'], {});
                }
            })
            .catch((reason: HttpErrorResponse) => {
                if (reason.status == 404) {
                    window.location.href = '/login';
                }else {
                    this.notificationService.displayErrorMessage("Error while retrieving current user.", reason.message);
                }

                return false;
            });
    }
}
