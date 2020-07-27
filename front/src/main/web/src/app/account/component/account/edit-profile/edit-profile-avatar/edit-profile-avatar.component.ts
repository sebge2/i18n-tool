import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from "../../../../../core/auth/model/user.model";
import {Subject} from "rxjs";
import {AuthenticationService} from "../../../../../core/auth/service/authentication.service";

@Component({
  selector: 'app-edit-profile-avatar',
  templateUrl: './edit-profile-avatar.component.html',
  styleUrls: ['./edit-profile-avatar.component.css']
})
export class EditProfileAvatarComponent implements OnInit, OnDestroy  {

  public currentUser: User;
  private readonly _destroyed$ = new Subject();

  constructor(private authenticationService: AuthenticationService) {
  }

  ngOnInit() {
    this.authenticationService.currentUser()
        .subscribe(currentUser => this.currentUser = currentUser);
  }

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  public getAvatarUrl(): string {
    return (this.currentUser != null)
        ? `url('/api/user/${this.currentUser.id}/avatar')`
        : null;
  }
}
