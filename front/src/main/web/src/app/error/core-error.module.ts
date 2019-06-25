import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import {Error403Component} from './component/error403/error403.component';
import {Error404Component} from './component/error404/error404.component';
import {ErrorStandardComponent} from './component/error-standard/error-standard.component';
import {ErrorMessageComponent} from './component/error-message/error-message.component';
import {CoreSharedModule} from "../core/shared/core-shared-module";

const appRoutes: Routes = [
    {
        path: '',
        children: [
            {path: '403', component: Error403Component},
            {path: '404', component: Error404Component},
            {path: '', component: ErrorStandardComponent},
            {path: ':statusCode', component: ErrorStandardComponent}
        ]
    }
];

@NgModule({
    declarations: [
        Error403Component,
        Error404Component,
        ErrorStandardComponent,
        ErrorMessageComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes),
        CoreSharedModule
    ],
    exports: [RouterModule]
})
export class CoreErrorModule {
}
