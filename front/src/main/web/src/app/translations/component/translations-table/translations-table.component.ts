import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsService} from '../../service/translations.service';
import {BundleFile} from "../../model/edition/bundle-file.model";
import {BundleKey} from '../../model/edition/bundle-key.model';
import {ALL_LOCALES} from "../../model/locale.model";

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent implements OnInit {

    @Input()
    private _searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    columns = [];
    displayedColumns: string[] = [];
    dataSource: (BundleFile | BundleKey)[] = [];

    constructor(private translationsService: TranslationsService) {
        this.columns.push(
            {columnDef: 'key', header: 'Key', cell: (bundleKey: BundleKey) => `${bundleKey.key}`}
        );

        for (const locale of ALL_LOCALES) {
            this.columns.push(
                {
                    columnDef: locale.toString(),
                    header: locale,
                    cell: (bundleKey: BundleKey) => `${bundleKey.findTranslation(locale).currentValue()}`
                }
            );
        }

        this.displayedColumns = this.columns.map(column => column.columnDef);
    }

    ngOnInit() {
    }

    get searchRequest(): TranslationsSearchRequest {
        return this._searchRequest;
    }

    @Input()
    set searchRequest(value: TranslationsSearchRequest) {
        this._searchRequest = value;

        if (this.searchRequest != null && this.searchRequest.workspace != null) {
            this.translationsService
                .getTranslations(this.searchRequest)
                .toPromise()
                .then(page => {
                    this.dataSource = [];
                    for (const file of page.files) {
                        this.dataSource.push(file);

                        for (const key of file.keys) {
                            this.dataSource.push(key);
                        }
                    }

                    console.log(this.dataSource);
                });
        }
    }

    isBundleFile(index, item): boolean {
        return item instanceof BundleFile;
    }
}
