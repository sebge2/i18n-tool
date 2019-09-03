import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MainMessageComponent} from './component/main-message/main-message.component';
import {WorkspaceIconPipe} from "./pipe/workspace-icon.pipe";
import {WorkspaceIconCssPipe} from "./pipe/workspace-icon-css.pipe";
import {LocaleIconPipe} from "./pipe/locale-icon.pipe";

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

        MainMessageComponent,
        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        LocaleIconPipe
    ]
})
export class CoreSharedModule {

}
