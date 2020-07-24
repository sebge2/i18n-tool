import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PreferencesComponent} from './component/preferences/preferences.component';
import {RouterModule, Routes} from "@angular/router";
import {CoreSharedModule} from "../core/shared/core-shared-module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CoreSharedLibModule} from "../core/shared/core-shared-lib.module";
import {CoreTranslationModule} from "../core/translation/core-translation-module";
import { EditProfileComponent } from './component/preferences/edit-profile/edit-profile.component';
import { EditPasswordComponent } from './component/preferences/edit-password/edit-password.component';
import { EditPreferencesComponent } from './component/preferences/edit-preferences/edit-preferences.component';

const appRoutes: Routes = [
    {path: '', pathMatch: 'full', component: PreferencesComponent}
];

@NgModule({
    declarations: [PreferencesComponent, EditProfileComponent, EditPasswordComponent, EditPreferencesComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes),
        ReactiveFormsModule,
        FormsModule,
        CoreSharedLibModule,
        CoreSharedModule,
        CoreTranslationModule
    ],
    exports: [RouterModule]
})
export class AccountModule {
}
