import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../service/authentication.service";

@Component({
    selector: 'app-logout',
    templateUrl: './logout.component.html',
    styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

    loggedOut: boolean = null;

    constructor(private authenticationService: AuthenticationService) {
    }

    ngOnInit() {
        this.authenticationService
            .logout()
            .then((_) => this.loggedOut = true)
            .catch((error: any) => this.loggedOut = false);
    }

}
