import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainComponent} from './component/main/main.component';
import {RouterModule} from "@angular/router";
import {MaterialModule} from './material.module';
import {MenuComponent} from "./component/menu/menu.component";
import {HeaderComponent} from "./component/header/header.component";
import {ReactiveFormsModule} from "@angular/forms";
import {FlexLayoutModule} from "@angular/flex-layout";

@NgModule({
    declarations: [
        MainComponent,
        MenuComponent,
        HeaderComponent
    ],
    imports: [
        RouterModule,
        CommonModule,
        MaterialModule
    ],
    exports: [
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule
    ]
})
export class CoreUiModule {

}
