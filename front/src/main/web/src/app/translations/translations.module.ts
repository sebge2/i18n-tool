import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationsComponent} from "./component/translations/translations.component";
import {RouterModule, Routes} from "@angular/router";
import {MaterialModule} from "../core/ui/material.module";
import { WorkspaceSelectorComponent } from './component/workspace-selector/workspace-selector.component';
import {ReactiveFormsModule} from "@angular/forms";

const appRoutes: Routes = [
    {
        path: '', component: TranslationsComponent
    }
];

@NgModule({
    declarations: [
        TranslationsComponent,
        WorkspaceSelectorComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes),
        MaterialModule,
        ReactiveFormsModule
    ],
    exports: [RouterModule]
})
export class TranslationsModule {

}
