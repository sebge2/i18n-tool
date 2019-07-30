import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Locale} from "../../model/locale.model";
import {Workspace} from "../../model/workspace.model";
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsSearchCriterion} from "../../model/translations-search-criterion.model";

@Component({
    selector: 'app-translations-search-bar',
    templateUrl: './translations-search-bar.component.html',
    styleUrls: ['./translations-search-bar.component.css']
})
export class TranslationsSearchBarComponent implements OnInit {

    @Output()
    searchRequestChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();

    searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    constructor() {
        this.searchRequest.criterion = TranslationsSearchCriterion.MISSING_TRANSLATIONS;
    }

    ngOnInit() {
    }

    onSelectedWorkspace(workspace: Workspace) {
        this.searchRequest.workspace = workspace;
    }

    onSelectedLocales(locales: Locale[]) {
        this.searchRequest.locales = locales;
    }

    onSelectedCriterion(criterion: TranslationsSearchCriterion) {
        this.searchRequest.criterion = criterion;
    }

    onSearch(){
        this.searchRequestChange.emit(this.searchRequest);
    }
}
