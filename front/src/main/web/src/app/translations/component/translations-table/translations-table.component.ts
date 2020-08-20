import {Component, Input} from '@angular/core';
import {FactService} from "../../../core/shared/component/scroller/fact.service";
import {FactsDataSource} from "../../../core/shared/component/scroller/scroller.component";
import {TranslationsSearchRequest} from "../../model/search/translations-search-request.model";

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent {

    public dataSource: FactsDataSource;

    private _searchRequest: TranslationsSearchRequest;

    constructor(private factService: FactService) {
        this.dataSource = new FactsDataSource(factService);
    }

    @Input()
    public get searchRequest(): TranslationsSearchRequest {
        return this._searchRequest;
    }

    public set searchRequest(request: TranslationsSearchRequest) {
        this._searchRequest = request;
    }
}
