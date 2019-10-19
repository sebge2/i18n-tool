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
                private authenticationService: AuthenticationService) {
    }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        return this.authenticationService.currentUser
            .pipe(
                map((user: User) => {
                        if (user == null) {
                            this.router.navigate(['/login'], {});
                        } else if (user.hasRole("MEMBER_OF_ORGANIZATION")) {
                            return true;
                        } else {
                            this.router.navigate(['/error', '403'], {});
                        }
                    }
                )
            );
    }
}
