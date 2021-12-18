import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../service/authentication.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { OAuthClient } from '../../../model/oauth-client.model';
import * as _ from 'lodash';

@Component({
  selector: 'app-login-provider',
  templateUrl: './login-provider.component.html',
  styleUrls: ['./login-provider.component.css'],
})
export class LoginProviderComponent implements OnInit, OnDestroy {
  private destroyed$ = new Subject<void>();

  private clients: OAuthClient[] = [];

  constructor(private authenticationService: AuthenticationService) {}

  ngOnInit() {
    this.authenticationService
      .getSupportedOauthClients()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((clients) => (this.clients = clients));
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  hasProvider(): boolean {
    return !_.isEmpty(this.clients);
  }

  isGoogleSupported(): boolean {
    return !!this.clients.find((client) => client == OAuthClient.GOOGLE);
  }

  isGitHubSupported(): boolean {
    return !!this.clients.find((client) => client == OAuthClient.GITHUB);
  }
}
