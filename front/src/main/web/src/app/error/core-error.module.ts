import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import {ErrorComponent} from './component/error/error.component';
import {Error403Component} from './component/error403/error403.component';
import {Error404Component} from './component/error404/error404.component';
import {ErrorStandardComponent} from './component/error-standard/error-standard.component';

const appRoutes: Routes = [
    {
        path: '', component: ErrorComponent,
        children: [
            {path: '403', component: Error403Component},
            {path: '404', component: Error404Component},
            {path: ':statusCode', component: ErrorStandardComponent}
        ]
    }
];

@NgModule({
    declarations: [ErrorComponent, Error403Component, Error404Component, ErrorStandardComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes)
    ],
    exports: [RouterModule]
})
export class CoreErrorModule {
}
