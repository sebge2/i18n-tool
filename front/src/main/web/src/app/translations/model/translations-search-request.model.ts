import {Locale} from "./locale.model";
import {Workspace} from "./workspace.model";
import {TranslationsSearchCriterion} from "./translations-search-criterion.model";

export class TranslationsSearchRequest {

    workspace: Workspace;
    locales: Locale[] = [];
    criterion: TranslationsSearchCriterion = TranslationsSearchCriterion.ALL;

}