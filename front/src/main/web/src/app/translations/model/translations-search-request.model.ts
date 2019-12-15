import {Locale} from "./locale.model";
import {Workspace} from "./workspace.model";
import {TranslationsSearchCriterion} from "./translations-search-criterion.model";

export class TranslationsSearchRequest {

    public workspace: Workspace;
    public locales: Locale[] = [];
    public criterion: TranslationsSearchCriterion = TranslationsSearchCriterion.ALL;

    constructor(searchRequest?: TranslationsSearchRequest) {
        if (searchRequest != null) {
            this.workspace = searchRequest.workspace;
            this.locales = (searchRequest.locales != null) ? searchRequest.locales.slice() : [];
            this.criterion = searchRequest.criterion;
        }
    }

    isValid(): boolean {
        return (this.workspace != null) && (this.locales != null) && (this.criterion != null);
    }
}
