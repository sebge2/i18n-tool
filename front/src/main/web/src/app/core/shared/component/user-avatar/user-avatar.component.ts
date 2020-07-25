import {Component, Input, OnInit} from '@angular/core';
import {User} from "../../../auth/model/user.model";

@Component({
  selector: 'app-user-avatar',
  templateUrl: './user-avatar.component.html',
  styleUrls: ['./user-avatar.component.css']
})
export class UserAvatarComponent implements OnInit {

  @Input() public user: User;

  constructor() { }

  ngOnInit() {
  }

  getUrl(): string {
    return (this.user != null)
        ? `/api/user/${this.user.id}/avatar`
        : null;
  }

  getDisplayName(): string {
    return (this.user) != null ? this.user.displayName : null;
  }

}
