import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MainMessageComponent} from './component/main-message/main-message.component';
import {TranslateModule} from '@ngx-translate/core';
import {CoreSharedLibModule} from './core-shared-lib.module';
import {CardGridComponent} from './component/card-grid/card-grid.component';
import {CardGridItemComponent} from './component/card-grid/card-grid-item/card-grid-item.component';
import {FormCancelButtonComponent} from './component/button/form-cancel-button/form-cancel-button.component';
import {FormSaveButtonComponent} from './component/button/form-save-button/form-save-button.component';
import {FormDeleteButtonComponent} from './component/button/form-delete-button/form-delete-button.component';
import {DragDropDirective} from './directive/drag-drop.directive';
import {FormUploadButtonComponent} from './component/button/form-upload-button/form-upload-button.component';
import {WizardStepComponent} from './component/wizard/wizard-step/wizard-step.component';
import {WizardComponent} from './component/wizard/wizard.component';
import {TabsComponent} from './component/tabs/tabs.component';
import {TabComponent} from './component/tabs/tab/tab.component';
import {ErrorMessageListComponent} from './component/error-message-list/error-message-list.component';
import {CardComponent} from './component/card/card.component';
import {CardActionsComponent} from './component/card/card-actions/card-actions.component';
import {CardContentComponent} from './component/card/card-content/card-content.component';
import {FormOpenTabButtonComponent} from './component/button/form-open-tab-button/form-open-tab-button.component';
import {CardHeaderActionsComponent} from './component/card/card-header-actions/card-header-actions.component';
import {MoreActionItemButtonComponent} from './component/button/more-actions-button/more-action-item-button/more-action-item-button.component';
import {MoreActionsButtonComponent} from './component/button/more-actions-button/more-actions-button.component';
import {SyncButtonComponent} from './component/button/sync-button/sync-button.component';
import {DownloadButtonComponent} from './component/button/download-button/download-button.component';
import {TreeComponent} from './component/tree/tree.component';
import {TreeNodeTemplateComponent} from './component/tree/tree-node-template/tree-node-template.component';
import {GeneratePasswordButtonComponent} from './component/button/generate-password-button/generate-password-button.component';
import {RowActionsComponent} from './component/row-actions/row-actions.component';
import {RowActionsElementComponent} from './component/row-actions/row-actions-element/row-actions-element.component';
import {RowActionsItemComponent} from './component/row-actions/row-actions-item/row-actions-item.component';
import {ExpansionPanelComponent} from './component/expansion-panel/expansion-panel.component';
import {ExpansionPanelHeaderComponent} from './component/expansion-panel/expansion-panel-header/expansion-panel-header.component';
import {ExpansionPanelContentComponent} from './component/expansion-panel/expansion-panel-content/expansion-panel-content.component';
import {FormSearchButtonComponent} from './component/button/form-search-button/form-search-button.component';
import {GitHubLinkButtonComponent} from './component/button/git-hub-link-button/git-hub-link-button.component';
import {RestoreButtonComponent} from './component/button/restore-button/restore-button.component';
import {RestoreButtonConfirmationComponent} from './component/button/restore-button/restore-button-confirmation/restore-button-confirmation.component';
import {DialogComponent} from './component/dialog/dialog.component';
import {DialogActionsComponent} from './component/dialog/dialog-actions/dialog-actions.component';
import {DialogContentComponent} from './component/dialog/dialog-content/dialog-content.component';
import {PublishButtonComponent} from './component/button/publish-button/publish-button.component';
import {FormSendButtonComponent} from './component/button/form-send-button/form-send-button.component';
import {FormSelectFieldComponent} from './component/field/form-select-field/form-select-field.component';
import {FormDeleteButtonConfirmationComponent} from './component/button/form-delete-button/form-delete-button-confirmation/form-delete-button-confirmation.component';
import {CountdownComponent} from './component/countdown/countdown.component';
import {BackwardButtonComponent} from './component/button/backward-button/backward-button.component';
import {ForwardButtonComponent} from './component/button/forward-button/forward-button.component';
import {VisualizeButtonComponent} from './component/button/visualize-button/visualize-button.component';
import {FormAddButtonComponent} from './component/button/form-add-button/form-add-button.component';
import {TableComponent} from './component/table/table.component';
import {TableCellComponent} from './component/table/table-cell/table-cell.component';
import {TableHeaderComponent} from './component/table/table-header/table-header.component';
import {TableTopHeaderRowComponent} from './component/table/table-top-header-row/table-top-header-row.component';
import {OnEnterDirective} from './directive/on-enter.directive';
import {BanButtonComponent} from './component/button/ban-button/ban-button.component';
import {PlayButtonComponent} from './component/button/play-button/play-button.component';
import {TooltipComponent} from './component/tooltip/tooltip.component';
import {TableExpandedRowComponent} from './component/table/table-expanded-row/table-expanded-row.component';
import {UnsavedLabelComponent} from './component/unsaved-label/unsaved-label.component';
import {TableRowComponent} from './component/table/table-row/table-row.component';
import {TableHeaderRowComponent} from './component/table/table-header-row/table-header-row.component';
import {UploadZoneComponent} from './component/upload-zone/upload-zone.component';
import {UploadZoneContentComponent} from './component/upload-zone/upload-zone-content/upload-zone-content.component';
import {ListOrganizerComponent} from './component/list-organizer/list-organizer.component';
import {FormApplyButtonComponent} from './component/button/form-apply-button/form-apply-button.component';
import { MenuContainerComponent } from './component/menu/menu-container.component';
import { MenuHeaderWrapper } from './component/menu/menu-header-wrapper/menu-header-wrapper.component';
import { MenuWrapperComponent } from './component/menu/menu-wrapper/menu-wrapper.component';
import { MenuMainWrapperComponent } from './component/menu/menu-main-wrapper/menu-main-wrapper.component';
import { MenuToolBarContainerComponent } from './component/tool-bar/menu-tool-bar-container.component';
import { MenuToolBarComponent } from './component/tool-bar/menu-tool-bar/menu-tool-bar.component';
import { MenuToolBarMainComponent } from './component/tool-bar/menu-tool-bar-main/menu-tool-bar-main.component';
import {MenuToolBarIconsComponent} from "./component/tool-bar/menu-tool-bar-icons/menu-tool-bar-icons.component";
import {GenericButtonComponent} from "./component/button/generic-button/generic-button.component";
import {IconComponent} from "./component/icon/icon.component";

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
        BanButtonComponent,
        PlayButtonComponent,
        GenericButtonComponent,

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
        TableComponent,
        TableHeaderRowComponent,
        TableHeaderComponent,
        TableRowComponent,
        TableCellComponent,
        TableTopHeaderRowComponent,
        TableExpandedRowComponent,
        TooltipComponent,
        UnsavedLabelComponent,
        UploadZoneComponent,
        UploadZoneContentComponent,
        ListOrganizerComponent,
        FormApplyButtonComponent,
        MenuContainerComponent,
        MenuHeaderWrapper,
        MenuWrapperComponent,
        MenuMainWrapperComponent,
        MenuToolBarContainerComponent,
        MenuToolBarComponent,
        MenuToolBarMainComponent,
        MenuToolBarIconsComponent,
        IconComponent,

        DragDropDirective,
        OnEnterDirective,
    ],
    imports: [CommonModule, CoreSharedLibModule, TranslateModule],
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
        BanButtonComponent,
        PlayButtonComponent,
        TooltipComponent,
        UnsavedLabelComponent,
        FormApplyButtonComponent,
        GenericButtonComponent,

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
        TableComponent,
        TableHeaderRowComponent,
        TableRowComponent,
        TableHeaderComponent,
        TableCellComponent,
        TableExpandedRowComponent,
        TableTopHeaderRowComponent,
        UploadZoneComponent,
        UploadZoneContentComponent,
        ListOrganizerComponent,
        MenuContainerComponent,
        MenuHeaderWrapper,
        MenuWrapperComponent,
        MenuMainWrapperComponent,
        MenuToolBarContainerComponent,
        MenuToolBarComponent,
        MenuToolBarMainComponent,
        MenuToolBarIconsComponent,
        IconComponent,

        DragDropDirective,
        OnEnterDirective,
    ],
})
export class CoreSharedModule {
}
