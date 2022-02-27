import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { combineLatest, from, Observable, of } from 'rxjs';
import { AuthenticationService } from '@i18n-core-auth';
import { flatMap } from 'rxjs/operators';
import { UserRole } from '@i18n-core-auth';
import { ToolLocaleService } from '@i18n-core-translation';

@Injectable({
  providedIn: 'root',
})
export class GlobalAuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private toolLocaleService: ToolLocaleService
  ) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    return combineLatest([this.authService.currentAuthenticatedUser(), this.toolLocaleService.getCurrentLocale()]).pipe(
      flatMap(([user, _]) => {
        if (user == null) {
          return from(this.router.navigate(['/login'], {}));
        } else if (user.hasRole(UserRole.MEMBER_OF_ORGANIZATION)) {
          return of(true);
        } else {
          return from(this.router.navigate(['/error', '403'], {}));
        }
      })
    );
  }
}
