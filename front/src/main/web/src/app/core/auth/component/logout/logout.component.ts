import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../service/authentication.service";
import {NotificationService} from "../../../notification/service/notification.service";

@Component({
    selector: 'app-logout',
    templateUrl: './logout.component.html',
    styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

    loggedOut: boolean = null;

    constructor(private authenticationService: AuthenticationService,
                private notificationService: NotificationService) {
    }

    ngOnInit() {
        this.authenticationService
            .logout()
            .then((_) => this.loggedOut = true)
            .catch((error: any) => {
                console.error('Error while login out user.', error);
                this.notificationService.displayErrorMessage('Error while login out user.');

                this.loggedOut = false;
            });
    }

}
