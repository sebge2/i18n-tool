import {Component, Inject, OnInit} from '@angular/core';
import {MAT_SNACK_BAR_DATA} from "@angular/material";

@Component({
    selector: 'app-error-notification',
    templateUrl: './error-notification.component.html',
    styleUrls: ['./error-notification.component.css']
})
export class ErrorNotificationComponent implements OnInit {

    constructor(@Inject(MAT_SNACK_BAR_DATA) public data: { message: string }) {
    }

    ngOnInit() {
    }

}