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

@NgModule({
    declarations: [
        MainMessageComponent,
        CardGridComponent,
        CardGridItemComponent,
        FormCancelButtonComponent,
        FormSaveButtonComponent,
        FormDeleteButtonComponent,
        FormUploadButtonComponent,
        WizardComponent,
        WizardStepComponent,
        TabsComponent,
        TabComponent,
        ErrorMessageListComponent,

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

        MainMessageComponent,
        CardGridComponent,
        CardGridItemComponent,
        FormCancelButtonComponent,
        FormSaveButtonComponent,
        FormDeleteButtonComponent,
        FormUploadButtonComponent,
        WizardComponent,
        WizardStepComponent,
        TabsComponent,
        TabComponent,
        ErrorMessageListComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,

        DragDropDirective,
    ]
})
export class CoreSharedModule {

}
