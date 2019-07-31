import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ALL_LOCALES, Locale} from "../../model/locale.model";
import {Workspace} from "../../model/workspace.model";
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsSearchCriterion} from "../../model/translations-search-criterion.model";
import {LocaleIconPipe} from "../../pipe/locale-icon.pipe";

@Component({
    selector: 'app-translations-search-bar',
    templateUrl: './translations-search-bar.component.html',
    styleUrls: ['./translations-search-bar.component.css'],
    providers: [LocaleIconPipe]
})
export class TranslationsSearchBarComponent implements OnInit {

    @Output()
    searchRequestChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();

    @Input()
    searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    constructor(private localeIconPipe: LocaleIconPipe) {
        this.searchRequest.criterion = TranslationsSearchCriterion.MISSING_TRANSLATIONS;
        this.searchRequest.locales = [Locale.FR, Locale.EN];
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

    title(): String {
        let title = "Search";

        switch (this.searchRequest.criterion) {
            case TranslationsSearchCriterion.ALL:
                title += " for <b>all</b> translations";
                break;
            case TranslationsSearchCriterion.MISSING_TRANSLATIONS:
                title += " for <b>missing</b> translations";
                break;
            case TranslationsSearchCriterion.TRANSLATIONS_CURRENT_USER_UPDATED:
                title += " for translations that <b>I have updated</b>";
                break;
            case TranslationsSearchCriterion.UPDATED_TRANSLATIONS:
                title += " for <b>updated</b> translations";
                break;
        }

        if (this.searchRequest.workspace != null) {
            title += " on <b>" + this.searchRequest.workspace.branch + "</b> branch";
        }

        if (this.searchRequest.locales.length == 0 || this.searchRequest.locales.length == ALL_LOCALES.length) {
            title += " for <b>all locales</b>";
        } else {
            title += " for locales ";

            for (let i = 0; i < this.searchRequest.locales.length; i++) {
                if (i == this.searchRequest.locales.length - 1 && i > 0) {
                    title += " and ";
                } else if (i > 0) {
                    title += ",";
                }

                title += " <span class=\"" + this.localeIconPipe.transform(this.searchRequest.locales[i]) + "\"></span>" + this.searchRequest.locales[i];
            }
        }

        return title + ".";
    }

    onSearch() {
        this.searchRequestChange.emit(this.searchRequest);
    }
}
