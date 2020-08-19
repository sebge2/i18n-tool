import {TranslationsSearchCriterion} from "./translations-search-criterion.model";
import {TranslationLocale} from "../translation-locale.model";
import {EnrichedWorkspace} from "../workspace/enriched-workspace.model";

export class TranslationsSearchRequest {

    public workspaces: EnrichedWorkspace[] = [];
    public locales: TranslationLocale[] = [];
    public criterion: TranslationsSearchCriterion = TranslationsSearchCriterion.ALL;

    constructor() {
    }
}
