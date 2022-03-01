import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import { LogoutComponent } from './component/logout/logout.component';
import { LoginComponent } from './component/login/login.component';
import { LoginUserPasswordComponent } from './component/login/login-user-password/login-user-password.component';
import { CoreSharedModule } from '@i18n-core-shared';
import { LoginProviderComponent } from './component/login/login-provider/login-provider.component';
import { HasRoleDirective } from './directive/has-role.directive';
import { HasRepositoryAccessDirective } from './directive/has-repository-access.directive';
import { LogoutGuard } from './service/guard/logout.guard';
import { LoginGuard } from './service/guard/login.guard';
import { CoreSharedLibModule } from '../shared/core-shared-lib.module';
import {LOGIN_PATH_SEGMENT, LOGOUT_PATH_SEGMENT} from './service/authentication.service';


const appRoutes: Routes = [
  {
    path: LOGOUT_PATH_SEGMENT,
    component: LogoutComponent,
    canActivate: [LogoutGuard],
  },
  {
    path: LOGIN_PATH_SEGMENT,
    component: LoginComponent,
    canActivate: [LoginGuard],
  },
];

@NgModule({
  declarations: [
    LogoutComponent,
    LoginComponent,
    LoginUserPasswordComponent,
    LoginProviderComponent,

    HasRoleDirective,
    HasRepositoryAccessDirective,
  ],
  imports: [RouterModule.forChild(appRoutes), HttpClientModule, CoreSharedModule, CoreSharedLibModule],
  exports: [RouterModule, HasRoleDirective, HasRepositoryAccessDirective],
})
export class CoreAuthModule {}
