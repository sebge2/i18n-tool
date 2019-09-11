import {Component, OnInit} from '@angular/core';
import {Locale} from "../../../translations/model/locale.model";

@Component({
    selector: 'app-settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

    languages: Locale[] = [];

    constructor() {
    }

    ngOnInit() {
    }

}
