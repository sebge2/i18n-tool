import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MainMessageComponent} from './component/main-message/main-message.component';
import {WorkspaceIconPipe} from "./pipe/workspace-icon.pipe";
import {WorkspaceIconCssPipe} from "./pipe/workspace-icon-css.pipe";
import {ToolLocaleIconPipe} from "./pipe/tool-locale-icon.pipe";
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedLibModule} from "./core-shared-lib.module";
import {TranslationLocaleIconPipe} from "./pipe/translation-locale-icon.pipe";
import {RepositoryIconPipe} from "./pipe/repository-icon.pipe";
import {CardGridComponent} from "./component/card-grid/card-grid.component";
import {CardGridItemComponent} from "./component/card-grid/card-grid-item/card-grid-item.component";
import {FormCancelButtonComponent} from "./component/form-cancel-button/form-cancel-button.component";
import {FormSaveButtonComponent} from "./component/form-save-button/form-save-button.component";
import {FormDeleteButtonComponent} from "./component/form-delete-button/form-delete-button.component";
import {DragDropDirective} from "./directive/drag-drop.directive";
import {FormUploadButtonComponent} from "./component/form-upload-button/form-upload-button.component";
import {WizardStepComponent} from "./component/wizard/wizard-step/wizard-step.component";
import {WizardComponent} from "./component/wizard/wizard.component";
import {TabsComponent} from "./component/tabs/tabs.component";
import {TabComponent} from "./component/tabs/tab/tab.component";
import {ErrorMessageListComponent} from "./component/error-message-list/error-message-list.component";
import {CardComponent} from "./component/card/card.component";
import {CardActionsComponent} from "./component/card/card-actions/card-actions.component";
import {CardContentComponent} from "./component/card/card-content/card-content.component";
import {FormOpenTabButtonComponent} from "./component/form-open-tab-button/form-open-tab-button.component";
import {CardHeaderActionsComponent} from "./component/card/card-header-actions/card-header-actions.component";
import {MoreActionItemButtonComponent} from "./component/more-actions-button/more-action-item-button/more-action-item-button.component";
import {MoreActionsButtonComponent} from "./component/more-actions-button/more-actions-button.component";
import {SyncButtonComponent} from "./component/sync-button/sync-button.component";
import {InitDownloadButtonComponent} from "./component/init-download-button/init-download-button.component";
import {TreeComponent} from "./component/tree/tree.component";
import {TreeNodeTemplateComponent} from "./component/tree/tree-node-template/tree-node-template.component";
import {GeneratePasswordButtonComponent} from "./component/generate-password-button/generate-password-button.component";
import {RowActionsComponent} from "./component/row-actions/row-actions.component";
import {RowActionsElementComponent} from "./component/row-actions/row-actions-element/row-actions-element.component";
import {RowActionsItemComponent} from "./component/row-actions/row-actions-item/row-actions-item.component";
import {ExpansionPanelComponent} from "./component/expansion-panel/expansion-panel.component";
import {ExpansionPanelHeaderComponent} from "./component/expansion-panel/expansion-panel-header/expansion-panel-header.component";
import {ExpansionPanelContentComponent} from "./component/expansion-panel/expansion-panel-content/expansion-panel-content.component";
import {FormSearchButtonComponent} from "./component/form-search-button/form-search-button.component";
import { GitHubLinkButtonComponent } from './component/git-hub-link-button/git-hub-link-button.component';
import {DialogComponent} from "./component/dialog/dialog.component";
import {DialogActionsComponent} from "./component/dialog/dialog-actions/dialog-actions.component";
import {DialogContentComponent} from "./component/dialog/dialog-content/dialog-content.component";
import {PublishButtonComponent} from "./component/publish-button/publish-button.component";
import {FormSendButtonComponent} from "./component/form-send-button/form-send-button.component";

@NgModule({
    declarations: [
        FormCancelButtonComponent,
        FormSaveButtonComponent,
        FormDeleteButtonComponent,
        FormUploadButtonComponent,
        FormOpenTabButtonComponent,
        FormSearchButtonComponent,
        FormSendButtonComponent,
        SyncButtonComponent,
        InitDownloadButtonComponent,
        MoreActionsButtonComponent,
        MoreActionItemButtonComponent,
        GeneratePasswordButtonComponent,
        GitHubLinkButtonComponent,
        PublishButtonComponent,

        MainMessageComponent,
        ErrorMessageListComponent,
        CardGridComponent,
        CardGridItemComponent,
        WizardComponent,
        WizardStepComponent,
        TabsComponent,
        TabComponent,
        CardComponent,
        CardActionsComponent,
        CardContentComponent,
        CardHeaderActionsComponent,
        TreeComponent,
        TreeNodeTemplateComponent,
        RowActionsComponent,
        RowActionsElementComponent,
        RowActionsItemComponent,
        RowActionsElementComponent,
        RowActionsItemComponent,
        ExpansionPanelComponent,
        ExpansionPanelHeaderComponent,
        ExpansionPanelContentComponent,
        DialogComponent,
        DialogActionsComponent,
        DialogContentComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,

        DragDropDirective,
    ],
    imports: [
        CommonModule,
        CoreSharedLibModule,
        TranslateModule,
    ],
    exports: [
        CommonModule,
        TranslateModule,
        CoreSharedLibModule,

        FormCancelButtonComponent,
        FormSaveButtonComponent,
        FormDeleteButtonComponent,
        FormUploadButtonComponent,
        FormOpenTabButtonComponent,
        FormSearchButtonComponent,
        FormSendButtonComponent,
        SyncButtonComponent,
        InitDownloadButtonComponent,
        MoreActionsButtonComponent,
        MoreActionItemButtonComponent,
        GeneratePasswordButtonComponent,
        GitHubLinkButtonComponent,
        PublishButtonComponent,

        MainMessageComponent,
        ErrorMessageListComponent,
        CardGridComponent,
        CardGridItemComponent,
        WizardComponent,
        WizardStepComponent,
        TabsComponent,
        TabComponent,
        CardComponent,
        CardActionsComponent,
        CardContentComponent,
        CardHeaderActionsComponent,
        TreeComponent,
        TreeNodeTemplateComponent,
        RowActionsComponent,
        RowActionsElementComponent,
        RowActionsItemComponent,
        ExpansionPanelComponent,
        ExpansionPanelHeaderComponent,
        ExpansionPanelContentComponent,
        DialogComponent,
        DialogActionsComponent,
        DialogContentComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,

        DragDropDirective,
    ],
    providers: [
        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,
    ]
})
export class CoreSharedModule {

}
