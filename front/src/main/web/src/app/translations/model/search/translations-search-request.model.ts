import {TranslationsSearchCriterion} from "./translations-search-criterion.model";
import {TranslationLocale} from "../translation-locale.model";
import {Workspace} from "../workspace/workspace.model";

export class TranslationsSearchPageSpec {

    constructor(public nextPage: boolean,
                public keyOtherPage: string) {
    }
}

export class TranslationsSearchRequest {

    constructor(public workspaces: Workspace[] = [],
                public locales: TranslationLocale[] = [],
                public criterion: TranslationsSearchCriterion = TranslationsSearchCriterion.ALL,
                public pageSpec?: TranslationsSearchPageSpec) {
    }

    public goToPreviousPage(firstKeyOfPage: string): TranslationsSearchRequest {
        return new TranslationsSearchRequest(
            this.workspaces,
            this.locales,
            this.criterion,
            new TranslationsSearchPageSpec(
                false,
                firstKeyOfPage
            )
        );
    }

    public goToNextPage(lastKeyOfPage: string): TranslationsSearchRequest {
        return new TranslationsSearchRequest(
            this.workspaces,
            this.locales,
            this.criterion,
            new TranslationsSearchPageSpec(
                true,
                lastKeyOfPage
            )
        );
    }
}
