import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslationLocaleSelectorComponent } from './component/translation-locale-selector/translation-locale-selector.component';
import { CoreSharedModule } from '@i18n-core-shared';
import { CoreSharedLibModule } from '../shared/core-shared-lib.module';
import { WorkspaceSelectorComponent } from './component/workspace-selector/workspace-selector.component';
import { WorkspacesStartReviewDialogComponent } from './component/workspaces-start-review-dialog/workspaces-start-review-dialog.component';
import { WorkspaceLabelComponent } from './component/workspace-label/workspace-label.component';
import { WorkspaceBundleFileSelectorComponent } from './component/workspace-bundle-file-selector/workspace-bundle-file-selector.component';
import {TranslationLocaleSingleSelectorComponent} from "./component/translation-locale-single-selector/translation-locale-single-selector.component";
import {WorkspaceIconPipe} from './pipe/workspace-icon.pipe';
import { LocalizedPipe } from '../translation/pipe/localized.pipe';
import { BundleFileIconPipe } from '../translation/pipe/bundle-file-icon.pipe';
import { BundleFileNamePipe } from '../translation/pipe/bundle-file-name.pipe';
import { TranslationLocaleIconPipe } from '../translation/pipe/translation-locale-icon.pipe';
import { RepositoryIconPipe } from '../translation/pipe/repository-icon.pipe';
import { WorkspaceIconCssPipe } from '../translation/pipe/workspace-icon-css.pipe';
import { ToolLocaleIconPipe } from '../translation/pipe/tool-locale-icon.pipe';

@NgModule({
  imports: [CommonModule, CoreSharedLibModule, CoreSharedModule],
  declarations: [
    TranslationLocaleSelectorComponent,
    WorkspaceSelectorComponent,
    WorkspacesStartReviewDialogComponent,
    WorkspaceLabelComponent,
    WorkspaceBundleFileSelectorComponent,
    TranslationLocaleSingleSelectorComponent,

    WorkspaceIconPipe,
    WorkspaceIconCssPipe,
    ToolLocaleIconPipe,
    TranslationLocaleIconPipe,
    RepositoryIconPipe,
    BundleFileIconPipe,
    BundleFileNamePipe,
    LocalizedPipe,
  ],
  exports: [
    TranslationLocaleSelectorComponent,
    WorkspaceSelectorComponent,
    WorkspacesStartReviewDialogComponent,
    WorkspaceLabelComponent,
    WorkspaceBundleFileSelectorComponent,
    TranslationLocaleSingleSelectorComponent,

    WorkspaceIconPipe,
    WorkspaceIconCssPipe,
    ToolLocaleIconPipe,
    TranslationLocaleIconPipe,
    RepositoryIconPipe,
    BundleFileIconPipe,
    BundleFileNamePipe,
    LocalizedPipe,
  ],
  providers: [
    WorkspaceIconPipe,
    WorkspaceIconCssPipe,
    ToolLocaleIconPipe,
    TranslationLocaleIconPipe,
    RepositoryIconPipe,
    LocalizedPipe,
  ]
})
export class CoreTranslationModule {}
