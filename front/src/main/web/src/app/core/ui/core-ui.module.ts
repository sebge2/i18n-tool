import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainComponent} from './component/main/main.component';
import {RouterModule} from '@angular/router';
import {MenuComponent} from './component/menu/menu.component';
import {HeaderComponent} from './component/header/header.component';
import {CoreAuthModule} from '@i18n-core-auth';
import {CoreSharedModule, TOOL_DESCRIPTOR_TOKEN, ToolDescriptor} from '@i18n-core-shared';
import {MenuItemComponent} from './component/menu/menu-item/menu-item.component';
import {HelpToolComponent} from './component/help/help-tool/help-tool.component';

export const HELP_TOOL_ID = 'help';

export const TOOL_BAR_DESCRIPTOR_PROVIDER = {
    provide: TOOL_DESCRIPTOR_TOKEN,
    useValue: new ToolDescriptor(
        HELP_TOOL_ID,
        100,
        'help',
        'SHARED.HELP.TOOL.TITLE',
        'SHARED.HELP.TOOL.DESCRIPTION',
        HelpToolComponent,
    ),
    multi: true
};

@NgModule({
    declarations: [MainComponent, MenuComponent, HeaderComponent, MenuItemComponent, HelpToolComponent],
    imports: [RouterModule, CommonModule, CoreSharedModule, CoreAuthModule],
    exports: [],
    providers: [
        TOOL_BAR_DESCRIPTOR_PROVIDER
    ],
})
export class CoreUiModule {
}
