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
    criteria: EventEmitter<TranslationsSearchRequest> = new EventEmitter();

    constructor() {
    }

    ngOnInit() {
    }

    onSelectedLocales(selectedLocales: Locale[]) {
        console.log("here", selectedLocales);
    }

    onSelectedWorkspace(workspace: Workspace) {
        console.log("here", workspace);
    }

    onSelectedCriterion(criterion: TranslationsSearchCriterion) {
        console.log("here", criterion);
    }
}
