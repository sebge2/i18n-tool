import {Component, Input} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/search/translations-search-request.model";
import {TranslationsDataSource} from "./translations.datasource";
import {TranslationService} from "../../service/translation.service";

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent {

    public dataSource: TranslationsDataSource;

    private _searchRequest: TranslationsSearchRequest;

    constructor(private _translationService: TranslationService) {
        this.dataSource = new TranslationsDataSource(_translationService);
    }

    @Input()
    public get searchRequest(): TranslationsSearchRequest {
        return this._searchRequest;
    }

    public set searchRequest(request: TranslationsSearchRequest) {
        this._searchRequest = request;

        this.dataSource.setRequest(request);
    }

    public trackByFn(indexInSource, index, item) {
        return indexInSource;
    }
}
