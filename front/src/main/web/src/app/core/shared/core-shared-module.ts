import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MainMessageComponent} from './component/main-message/main-message.component';
import {WorkspaceIconPipe} from "./pipe/workspace-icon.pipe";
import {WorkspaceIconCssPipe} from "./pipe/workspace-icon-css.pipe";
import {ToolLocaleIconPipe} from "./pipe/tool-locale-icon.pipe";
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedLibModule} from "./core-shared-lib.module";
import {TranslationLocaleIconPipe} from "./pipe/translation-locale-icon.pipe";

@NgModule({
    declarations: [
        MainMessageComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe
    ],
    imports: [
        CommonModule
    ],
    exports: [
        CommonModule,
        TranslateModule,
        CoreSharedLibModule,

        MainMessageComponent,
        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        ToolLocaleIconPipe,
        TranslationLocaleIconPipe
    ]
})
export class CoreSharedModule {

}
