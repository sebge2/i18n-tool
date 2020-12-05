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
import {DownloadButtonComponent} from "./component/download-button/download-button.component";
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
import {GitHubLinkButtonComponent} from './component/git-hub-link-button/git-hub-link-button.component';
import { RestoreButtonComponent } from './component/restore-button/restore-button.component';
import { RestoreButtonConfirmationComponent } from './component/restore-button/restore-button-confirmation/restore-button-confirmation.component';
import {DialogComponent} from "./component/dialog/dialog.component";
import {DialogActionsComponent} from "./component/dialog/dialog-actions/dialog-actions.component";
import {DialogContentComponent} from "./component/dialog/dialog-content/dialog-content.component";
import {PublishButtonComponent} from "./component/publish-button/publish-button.component";
import {FormSendButtonComponent} from "./component/form-send-button/form-send-button.component";
import {FormSelectFieldComponent} from "./component/form-select-field/form-select-field.component";
import {FormDeleteButtonConfirmationComponent} from "./component/form-delete-button/form-delete-button-confirmation/form-delete-button-confirmation.component";
import {CountdownComponent} from "./component/countdown/countdown.component";
import {BackwardButtonComponent} from "./component/backward-button/backward-button.component";
import {ForwardButtonComponent} from "./component/forward-button/forward-button.component";
import {BundleFileIconPipe} from "./pipe/bundle-file-icon.pipe";
import {BundleFileNamePipe} from "./pipe/bundle-file-name.pipe";
import {VisualizeButtonComponent} from "./component/visualize-button/visualize-button.component";
import {FormAddButtonComponent} from "./component/form-add-button/form-add-button.component";

@NgModule({
    declarations: [
        FormCancelButtonComponent,
        FormSaveButtonComponent,
        FormDeleteButtonComponent,
        FormDeleteButtonConfirmationComponent,
        FormUploadButtonComponent,
        FormOpenTabButtonComponent,
        FormSearchButtonComponent,
        FormSendButtonComponent,
        FormAddButtonComponent,
        SyncButtonComponent,
        DownloadButtonComponent,
        MoreActionsButtonComponent,
        MoreActionItemButtonComponent,
        GeneratePasswordButtonComponent,
        GitHubLinkButtonComponent,
        PublishButtonComponent,
        BackwardButtonComponent,
        ForwardButtonComponent,
        VisualizeButtonComponent,
        RestoreButtonComponent,
        RestoreButtonConfirmationComponent,

        FormSelectFieldComponent,

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

        CountdownComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,
        BundleFileIconPipe,
        BundleFileNamePipe,

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
        FormDeleteButtonConfirmationComponent,
        FormUploadButtonComponent,
        FormOpenTabButtonComponent,
        FormSearchButtonComponent,
        FormSendButtonComponent,
        FormAddButtonComponent,
        SyncButtonComponent,
        DownloadButtonComponent,
        MoreActionsButtonComponent,
        MoreActionItemButtonComponent,
        GeneratePasswordButtonComponent,
        GitHubLinkButtonComponent,
        PublishButtonComponent,
        BackwardButtonComponent,
        ForwardButtonComponent,
        VisualizeButtonComponent,
        RestoreButtonComponent,
        RestoreButtonConfirmationComponent,

        FormSelectFieldComponent,

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

        CountdownComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,
        BundleFileIconPipe,
        BundleFileNamePipe,

        DragDropDirective,
    ],
    providers: [
        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,
    ],
    entryComponents: [
        FormDeleteButtonConfirmationComponent,
        RestoreButtonConfirmationComponent,
    ]
})
export class CoreSharedModule {

}
