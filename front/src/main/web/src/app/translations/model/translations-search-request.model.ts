import {ALL_LOCALES, Locale} from "./locale.model";
import {Workspace} from "./workspace.model";
import {TranslationsSearchCriterion} from "./translations-search-criterion.model";

export class TranslationsSearchRequest {

    workspace: Workspace;
    locales: Locale[] = [];
    criterion: TranslationsSearchCriterion = TranslationsSearchCriterion.ALL;

    constructor(searchRequest?: TranslationsSearchRequest) {
        if (searchRequest != null) {
            this.workspace = searchRequest.workspace;
            this.locales = (searchRequest.locales != null) ? searchRequest.locales.slice() : [];
            this.criterion = searchRequest.criterion;
        }
    }

    usedLocales(): Locale[] {
        return this.locales.length == 0 ? ALL_LOCALES : this.locales;
    }

    isValid(): boolean {
        return (this.workspace != null) && (this.locales != null) && (this.criterion != null);
    }
}
