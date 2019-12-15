import {Component, OnInit} from '@angular/core';
import {TranslationLocale} from "../../../translations/model/translation-locale.model";

@Component({
    selector: 'app-settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

    languages: TranslationLocale[] = [];

    constructor() {
    }

    ngOnInit() {
    }

}
