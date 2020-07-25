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
import {ImagePreloadDirective} from "./directive/image-preload.directive";
import {CardGridComponent} from "./component/card-grid/card-grid.component";
import {CardGridItemComponent} from "./component/card-grid/card-grid-item/card-grid-item.component";
import {FormCancelButtonComponent} from "./component/form-cancel-button/form-cancel-button.component";
import {FormSaveButtonComponent} from "./component/form-save-button/form-save-button.component";
import {FormDeleteButtonComponent} from "./component/form-delete-button/form-delete-button.component";
import {UserAvatarComponent} from "./component/user-avatar/user-avatar.component";

@NgModule({
    declarations: [
        MainMessageComponent,
        CardGridComponent,
        CardGridItemComponent,
        FormCancelButtonComponent,
        FormSaveButtonComponent,
        FormDeleteButtonComponent,
        UserAvatarComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,
        ImagePreloadDirective,
    ],
    imports: [
        CommonModule,
        CoreSharedLibModule,
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
        UserAvatarComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe,
        RepositoryIconPipe,
        ImagePreloadDirective,
    ]
})
export class CoreSharedModule {

}
