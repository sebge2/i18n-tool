import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountComponent } from './component/account/account.component';
import { RouterModule, Routes } from '@angular/router';
import { CoreSharedModule } from '@i18n-core-shared';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CoreSharedLibModule } from '../core/shared/core-shared-lib.module';
import { CoreTranslationModule } from '@i18n-core-translation';
import { EditProfileComponent } from './component/account/edit-profile/edit-profile.component';
import { EditPasswordComponent } from './component/account/edit-password/edit-password.component';
import { EditPreferencesComponent } from './component/account/edit-preferences/edit-preferences.component';
import { EditProfileAvatarComponent } from './component/account/edit-profile/edit-profile-avatar/edit-profile-avatar.component';

const appRoutes: Routes = [{ path: '', pathMatch: 'full', component: AccountComponent }];

@NgModule({
  declarations: [
    AccountComponent,
    EditProfileComponent,
    EditPasswordComponent,
    EditPreferencesComponent,
    EditProfileAvatarComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(appRoutes),
    ReactiveFormsModule,
    FormsModule,
    CoreSharedLibModule,
    CoreSharedModule,
    CoreTranslationModule,
  ],
  exports: [RouterModule],
})
export class AccountModule {}
