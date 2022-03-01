import { Injectable } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import {mergeMap, Observable, of} from 'rxjs';
import { AuthenticationService } from '../authentication.service';
import { AuthenticatedUser } from '../../model/authenticated-user.model';
import {RedirectService} from "../redirect.service";

@Injectable({
  providedIn: 'root',
})
export class LogoutGuard implements CanActivate {
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authenticationService: AuthenticationService,
    private _redirectService: RedirectService,
  ) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.authenticationService.currentAuthenticatedUser().pipe(
      mergeMap((user: AuthenticatedUser) => {
        if (user != null) {
          return of(true);
        } else {
          console.debug('There is no connected user, go to login page instead.');

          return this._redirectService.redirectToLogin();
        }
      })
    );
  }
}
