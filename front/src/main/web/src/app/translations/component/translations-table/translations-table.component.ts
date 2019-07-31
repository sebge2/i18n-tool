import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsService} from '../../service/translations.service';
import {BundleKeysPage} from "../../model/edition/bundle-keys-page.model";

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent implements OnInit, OnChanges {

    @Input()
    searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    bundleKeysPage: BundleKeysPage;

    constructor(private translationsService: TranslationsService) {
    }

    ngOnInit() {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.searchRequest != null && this.searchRequest.workspace != null) {
            this.translationsService
                .getTranslations(this.searchRequest)
                .toPromise()
                .then(page => this.bundleKeysPage = page);
        }
    }

}
