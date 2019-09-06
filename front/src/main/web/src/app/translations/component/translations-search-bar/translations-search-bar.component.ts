import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ALL_LOCALES, Locale} from "../../model/locale.model";
import {Workspace} from "../../model/workspace.model";
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsSearchCriterion} from "../../model/translations-search-criterion.model";
import {LocaleIconPipe} from "../../../core/shared/pipe/locale-icon.pipe";

@Component({
    selector: 'app-translations-search-bar',
    templateUrl: './translations-search-bar.component.html',
    styleUrls: ['./translations-search-bar.component.css'],
    providers: [LocaleIconPipe]
})
export class TranslationsSearchBarComponent implements OnInit {

    @Output()
    expandedChange: EventEmitter<Boolean> = new EventEmitter();

    @Output()
    requestChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();

    @Output()
    requestInitChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();

    @Output()
    onSearchChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();

    searchRequest: TranslationsSearchRequest;

    private _expanded: boolean;

    constructor(private localeIconPipe: LocaleIconPipe) {
        this.searchRequest = new TranslationsSearchRequest();
    }

    ngOnInit() {
    }

    get expanded(): boolean {
        return this._expanded;
    }

    @Input()
    set expanded(value: boolean) {
        this._expanded = value;
        this.expandedChange.emit(this.expanded);
    }

    onSelectedWorkspace(workspace: Workspace) {
        setTimeout(() => {
                const notFullyLoaded = !this.isFullyLoaded();

                this.searchRequest.workspace = workspace;

                if (notFullyLoaded && this.isFullyLoaded()) {
                    this.requestInitChange.emit(this.searchRequest);
                }

                this.requestChange.emit(this.searchRequest);
            },
            0
        );
    }

    onSelectedLocales(locales: Locale[]) {
        setTimeout(() => {
                const notFullyLoaded = !this.isFullyLoaded();

                this.searchRequest.locales = locales;

                if (notFullyLoaded && this.isFullyLoaded()) {
                    this.requestInitChange.emit(this.searchRequest);
                }

                this.requestChange.emit(this.searchRequest);
            },
            0
        );
    }

    onSelectedCriterion(criterion: TranslationsSearchCriterion) {
        setTimeout(() => {
                const notFullyLoaded = !this.isFullyLoaded();

                this.searchRequest.criterion = criterion;

                if (notFullyLoaded && this.isFullyLoaded()) {
                    this.requestInitChange.emit(this.searchRequest);
                }

                this.requestChange.emit(this.searchRequest);
            },
            0
        );
    }

    onSearch() {
        this.onSearchChange.emit(new TranslationsSearchRequest(this.searchRequest));
    }

    isSearchForAllLocales(): boolean {
        return this.searchRequest.locales.length == 0 || this.searchRequest.locales.length == ALL_LOCALES.length;
    }

    getLocalesAsHtmlList(): String {
        let title = "";
        for (let i = 0; i < this.searchRequest.locales.length; i++) {
            if (i == this.searchRequest.locales.length - 1 && i > 0) {
                title += " and ";
            } else if (i > 0) {
                title += ",";
            }

            title += " <span class=\"" + this.localeIconPipe.transform(this.searchRequest.locales[i]) + "\"></span>" + this.searchRequest.locales[i].toString().toUpperCase();
        }

        return title;
    }

    private isFullyLoaded() {
        return !(this.searchRequest.workspace == null || this.searchRequest.locales == null || this.searchRequest.criterion == null);
    }
}
