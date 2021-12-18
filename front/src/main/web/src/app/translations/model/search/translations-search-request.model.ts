import { TranslationsSearchCriterion } from './translations-search-criterion.model';
import { TranslationLocale } from '@i18n-core-translation';
import { Workspace } from '@i18n-core-translation';
import { BundleFile } from '@i18n-core-translation';
import { TranslationKeyPattern } from './translation-key-pattern.model';

export class TranslationsSearchPageSpec {
  constructor(public nextPage: boolean, public keyOtherPage: string) {}
}

export class TranslationsSearchRequest {
  constructor(
    public workspaces: Workspace[] = [],
    public bundleFile: BundleFile | undefined,
    public locales: TranslationLocale[] = [],
    public criterion: TranslationsSearchCriterion = TranslationsSearchCriterion.ALL,
    public keyPattern?: TranslationKeyPattern,
    public pageSpec?: TranslationsSearchPageSpec
  ) {}

  goToPreviousPage(firstKeyOfPage: string): TranslationsSearchRequest {
    return new TranslationsSearchRequest(
      this.workspaces,
      this.bundleFile,
      this.locales,
      this.criterion,
      this.keyPattern,
      new TranslationsSearchPageSpec(false, firstKeyOfPage)
    );
  }

  goToNextPage(lastKeyOfPage: string): TranslationsSearchRequest {
    return new TranslationsSearchRequest(
      this.workspaces,
      this.bundleFile,
      this.locales,
      this.criterion,
      this.keyPattern,
      new TranslationsSearchPageSpec(true, lastKeyOfPage)
    );
  }
}
