import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../service/authentication.service';
import { NotificationService } from '@i18n-core-notification';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css'],
})
export class LogoutComponent implements OnInit {
  loggedOut: boolean = null;

  constructor(
    private _authenticationService: AuthenticationService,
    private _notificationService: NotificationService
  ) {}

  ngOnInit() {
    this._authenticationService
      .logout()
      .then((_) => (this.loggedOut = true))
      .catch((error: any) => {
        console.error('Error while login out user.', error);
        this._notificationService.displayErrorMessage('Error while login out user.');

        this.loggedOut = false;
      });
  }
}
