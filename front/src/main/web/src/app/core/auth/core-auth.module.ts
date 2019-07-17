import {NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule, Routes} from "@angular/router";
import {LogoutComponent} from './component/logout/logout.component';

const appRoutes: Routes = [
    {
        path: '',
        children: [
            {path: 'success', component: LogoutComponent}
        ]
    }
];

@NgModule({
    declarations: [LogoutComponent],
    imports: [
        HttpClientModule,
        RouterModule.forChild(appRoutes)
    ],
    exports: []
})
export class CoreAuthModule {

}
