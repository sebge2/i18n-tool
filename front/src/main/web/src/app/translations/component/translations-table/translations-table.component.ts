import {Component, Input, OnInit} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent implements OnInit {

    @Input()
    searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    constructor() {
    }

    ngOnInit() {
    }

}
