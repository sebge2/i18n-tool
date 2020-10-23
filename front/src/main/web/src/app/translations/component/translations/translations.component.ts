import {Component} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/search/translations-search-request.model";
import {TranslationsPage} from "../../model/search/translations-page.model";
import {TranslationsTableState} from "../../model/search/translation-search-state.model";

@Component({
    selector: 'app-translations',
    templateUrl: './translations.component.html',
    styleUrls: ['./translations.component.css']
})
export class TranslationsComponent {

    public expanded: boolean = true;
    public readonly tableState = new TranslationsTableState();

    public page: TranslationsPage;

    constructor() {
    }

    public onSearch(searchRequest: TranslationsSearchRequest) {
        this.expanded = false;
        setTimeout(
            () => this.tableState.updateSearchRequest(searchRequest),
            300
        )
    }

    public onPage(page: TranslationsPage) {
        this.page = page;
    }
}
