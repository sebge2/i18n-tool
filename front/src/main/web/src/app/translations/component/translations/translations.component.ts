import {Component} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/search/translations-search-request.model";

@Component({
    selector: 'app-translations',
    templateUrl: './translations.component.html',
    styleUrls: ['./translations.component.css']
})
export class TranslationsComponent {

    public searchRequest: TranslationsSearchRequest;
    public expanded: boolean = true;

    constructor() {
    }

    public onSearch(searchRequest: TranslationsSearchRequest) {
        this.searchRequest = searchRequest;
        this.expanded = false;
    }
}
