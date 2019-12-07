import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MainMessageComponent} from './component/main-message/main-message.component';
import {WorkspaceIconPipe} from "./pipe/workspace-icon.pipe";
import {WorkspaceIconCssPipe} from "./pipe/workspace-icon-css.pipe";
import {LocaleIconPipe} from "./pipe/locale-icon.pipe";
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedLibModule} from "./core-shared-lib.module";

@NgModule({
    declarations: [
        MainMessageComponent,

        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        LocaleIconPipe,
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
        LocaleIconPipe
    ]
})
export class CoreSharedModule {

}
