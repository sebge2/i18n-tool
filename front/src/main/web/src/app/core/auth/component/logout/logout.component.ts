import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../service/authentication.service";

@Component({
    selector: 'app-logout',
    templateUrl: './logout.component.html',
    styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

    private loggedOut: boolean = null;

    constructor(private authenticationService: AuthenticationService) {
    }

    ngOnInit() {
        this.authenticationService
            .logout()
            .subscribe(
                (result: any) => {
                    this.loggedOut = true;
                },
                (error: any) => {
                    this.loggedOut = false;
                }
            )
    }

}
