import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import {WorkspaceTableComponent} from './component/workspace-table/workspace-table.component';
import {RepositoryInitializerComponent} from './component/repository-initializer/repository-initializer.component';
import {CoreSharedModule} from "../core/shared/core-shared-module";
import {ConfirmWorkspaceDeletionComponent} from './component/workspace-table/confirm-deletion/confirm-workspace-deletion.component';
import {CoreAuthModule} from "../core/auth/core-auth.module";
import {UsersComponent} from './component/users/users.component';
import {RepositoriesComponent} from './component/repositories/repositories.component';
import {LocalesComponent} from './component/locales/locales.component';
import {LocaleViewCardComponent} from './component/locales/locale-view-card/locale-view-card.component';
import { RepositoryViewCardComponent } from './component/repositories/repository-view-card/repository-view-card.component';
import { UserViewCardComponent } from './component/users/user-view-card/user-view-card.component';
import { RepositoryDetailsComponent } from './component/repositories/repository-details/repository-details.component';
import { RepositoryListComponent } from './component/repositories/repository-list/repository-list.component';
import { RepositoryAddWizardComponent } from './component/repositories/repository-list/repository-add-wizard/repository-add-wizard.component';
import { RepositoryAddWizardStepTypeComponent } from './component/repositories/repository-list/repository-add-wizard/repository-add-wizard-step-type/repository-add-wizard-step-type.component';
import { RepositoryAddWizardStepInfoComponent } from './component/repositories/repository-list/repository-add-wizard/repository-add-wizard-step-info/repository-add-wizard-step-info.component';

const appRoutes: Routes = [
    {path: 'users', component: UsersComponent},
    {path: 'repositories', component: RepositoriesComponent},
    {path: 'locales', component: LocalesComponent}
];

@NgModule({
    declarations: [
        WorkspaceTableComponent,
        RepositoryInitializerComponent,
        ConfirmWorkspaceDeletionComponent,
        LocalesComponent,
        LocaleViewCardComponent,

        UsersComponent,
        UserViewCardComponent,
        RepositoryDetailsComponent,

        RepositoriesComponent,
        RepositoryListComponent,
        RepositoryViewCardComponent,
        RepositoryAddWizardComponent,
        RepositoryAddWizardStepTypeComponent,
        RepositoryAddWizardStepInfoComponent
    ],
    entryComponents: [
        ConfirmWorkspaceDeletionComponent,
        RepositoryAddWizardComponent
    ],
    imports: [
        CommonModule,
        CoreSharedModule,
        CoreAuthModule,

        RouterModule.forChild(appRoutes)
    ],
    exports: [RouterModule]
})
export class AdminModule {
}
