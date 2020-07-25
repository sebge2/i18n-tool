import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {User} from "../../../../core/auth/model/user.model";

@Component({
  selector: 'app-user-view-card',
  templateUrl: './user-view-card.component.html',
  styleUrls: ['./user-view-card.component.css']
})
export class UserViewCardComponent implements OnInit {

  @Input() public user: User;
  @Output() public save = new EventEmitter<User>();

  constructor() { }

  ngOnInit() {
  }

    getUrl() {
      return (this.user != null)
          ? `/api/user/${this.user.id}/avatar`
          : null;
    }
}
