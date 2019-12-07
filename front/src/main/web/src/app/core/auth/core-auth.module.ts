import {NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule, Routes} from "@angular/router";
import {LogoutComponent} from './component/logout/logout.component';
import {LoginComponent} from './component/login/login.component';
import {LoginUserPasswordComponent} from './component/login/login-user-password/login-user-password.component';
import {CoreSharedModule} from "../shared/core-shared-module";
import {LoginAuthKeyComponent} from './component/login/login-auth-key/login-auth-key.component';
import {LoginProviderComponent} from './component/login/login-provider/login-provider.component';
import {HasRoleDirective} from "./directive/has-role.directive";
import {LogoutGuard} from "./service/guard/logout.guard";
import {LoginGuard} from "./service/guard/login.guard";
import {CoreSharedLibModule} from "../shared/core-shared-lib.module";

const appRoutes: Routes = [
    {
        path: 'logout',
        component: LogoutComponent,
        canActivate: [LogoutGuard]
    },
    {
        path: 'login',
        component: LoginComponent,
        canActivate: [LoginGuard],
    }
];

@NgModule({
    declarations: [
        LogoutComponent,
        LoginComponent,
        LoginUserPasswordComponent,
        LoginAuthKeyComponent,
        LoginProviderComponent,

        HasRoleDirective
    ],
    imports: [
        RouterModule.forChild(appRoutes),
        HttpClientModule,
        CoreSharedModule,
        CoreSharedLibModule
    ],
    exports: [
        RouterModule,
        HasRoleDirective
    ]
})
export class CoreAuthModule {

}
