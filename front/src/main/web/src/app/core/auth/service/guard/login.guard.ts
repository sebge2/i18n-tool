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
import {AuthenticatedUser} from '../../model/authenticated-user.model';

@Injectable({
    providedIn: 'root'
})
export class LoginGuard implements CanActivate {

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private authenticationService: AuthenticationService) {
    }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.authenticationService.currentAuthenticatedUser()
            .pipe(
                map((user: AuthenticatedUser) => {
                        if (user != null) {
                            console.debug('There is a connected user, go to homepage instead.', user);

                            this.router.navigate(['/translations'], {relativeTo: this.activatedRoute});

                            return false;
                        } else {
                            return true;
                        }
                    }
                )
            );
    }

}
