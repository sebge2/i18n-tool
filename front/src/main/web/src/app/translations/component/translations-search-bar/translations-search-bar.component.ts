import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {TranslationsSearchCriteria} from "../../model/translations-search-criteria.model";
import {Locale} from "../../model/locale.model";
import {Workspace} from "../../model/workspace.model";

@Component({
    selector: 'app-translations-search-bar',
    templateUrl: './translations-search-bar.component.html',
    styleUrls: ['./translations-search-bar.component.css']
})
export class TranslationsSearchBarComponent implements OnInit {

    @Output()
    criteria: EventEmitter<TranslationsSearchCriteria> = new EventEmitter();

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
}
