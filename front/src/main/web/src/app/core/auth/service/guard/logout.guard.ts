import { Injectable } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../authentication.service';
import { map } from 'rxjs/operators';
import { AuthenticatedUser } from '../../model/authenticated-user.model';

@Injectable({
  providedIn: 'root',
})
export class LogoutGuard implements CanActivate {
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authenticationService: AuthenticationService
  ) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.authenticationService.currentAuthenticatedUser().pipe(
      map((user: AuthenticatedUser) => {
        if (user != null) {
          return true;
        } else {
          console.debug('There is no connected user, go to login page instead.');

          this.router.navigate(['/login']);

          return false;
        }
      })
    );
  }
}
