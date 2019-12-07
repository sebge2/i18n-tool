import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainComponent} from './component/main/main.component';
import {RouterModule} from "@angular/router";
import {MenuComponent} from "./component/menu/menu.component";
import {HeaderComponent} from "./component/header/header.component";
import {CoreAuthModule} from "../auth/core-auth.module";
import {CoreSharedModule} from "../shared/core-shared-module";

@NgModule({
    declarations: [
        MainComponent,
        MenuComponent,
        HeaderComponent
    ],
    imports: [
        RouterModule,
        CommonModule,
        CoreSharedModule,
        CoreAuthModule
    ],
    exports: []
})
export class CoreUiModule {

}
