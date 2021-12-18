import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { AuthenticationService } from '@i18n-core-auth';
import { User } from '@i18n-core-auth';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css'],
})
export class AccountComponent implements OnInit, OnDestroy {
  public currentUser: User;

  private readonly _destroyed$ = new Subject();

  constructor(private _authenticationService: AuthenticationService) {}

  ngOnInit() {
    this._authenticationService.currentUser().subscribe((currentUser) => (this.currentUser = currentUser));
  }

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }
}
