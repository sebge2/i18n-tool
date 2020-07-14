import {Component, OnInit} from '@angular/core';
import {TranslationLocale} from "../../../translations/model/translation-locale.model";

@Component({
    selector: 'app-preferences',
    templateUrl: './preferences.component.html',
    styleUrls: ['./preferences.component.css']
})
export class PreferencesComponent implements OnInit {

    languages: TranslationLocale[] = [];

    constructor() {
    }

    ngOnInit() {
    }

}
