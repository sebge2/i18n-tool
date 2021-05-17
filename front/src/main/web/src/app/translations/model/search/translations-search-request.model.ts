import {TranslationsSearchCriterion} from "./translations-search-criterion.model";
import {TranslationLocale} from "../translation-locale.model";
import {Workspace} from "../workspace/workspace.model";
import {BundleFile} from "../workspace/bundle-file.model";
import {TranslationKeyPattern} from "./translation-key-pattern.model";
import {mapAll, mapToSingleton} from "../../../core/shared/utils/collection-utils";
import * as _ from "lodash";

export class TranslationsSearchPageSpec {

    constructor(public nextPage: boolean,
                public keyOtherPage: string) {
    }
}

export class TranslationsSearchRequest {

    constructor(public workspaces: Workspace[] = [],
                public bundleFile: BundleFile | undefined,
                public locales: TranslationLocale[] = [],
                public criterion: TranslationsSearchCriterion = TranslationsSearchCriterion.ALL,
                public keyPattern?: TranslationKeyPattern,
                public pageSpec?: TranslationsSearchPageSpec) {
    }

    public goToPreviousPage(firstKeyOfPage: string): TranslationsSearchRequest {
        return new TranslationsSearchRequest(
            this.workspaces,
            this.bundleFile,
            this.locales,
            this.criterion,
            this.keyPattern,
            new TranslationsSearchPageSpec(
                false,
                firstKeyOfPage
            )
        );
    }

    public goToNextPage(lastKeyOfPage: string): TranslationsSearchRequest {
        return new TranslationsSearchRequest(
            this.workspaces,
            this.bundleFile,
            this.locales,
            this.criterion,
            this.keyPattern,
            new TranslationsSearchPageSpec(
                true,
                lastKeyOfPage
            )
        );
    }
}
