import {Injectable} from '@angular/core';
import {
    ActivatedRoute,
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot,
    UrlTree
} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthenticationService} from "../authentication.service";
import {map} from "rxjs/operators";
import {User} from "../../model/user.model";

@Injectable({
    providedIn: 'root'
})
export class LogoutGuard implements CanActivate {

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private authenticationService: AuthenticationService) {
    }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.authenticationService.currentUser
            .pipe(
                map((user: User) => {
                        if (user != null) {
                            return true;
                        } else {
                            console.debug('There is no connected user, go to login page instead.', user);

                            this.router.navigate(['/login']);

                            return false;
                        }
                    }
                )
            );
    }

}
