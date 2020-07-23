import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import {WorkspaceTableComponent} from './component/workspace-table/workspace-table.component';
import {RepositoryInitializerComponent} from './component/repository-initializer/repository-initializer.component';
import {CoreSharedModule} from "../core/shared/core-shared-module";
import {ConfirmWorkspaceDeletionComponent} from './component/workspace-table/confirm-deletion/confirm-workspace-deletion.component';
import {CoreAuthModule} from "../core/auth/core-auth.module";
import {UserTableComponent} from './component/user-table/user-table.component';
import {UserTableDetailsComponent} from './component/user-table/user-table-details/user-table-details.component';
import {UsersComponent} from './component/users/users.component';
import {RepositoriesComponent} from './component/repositories/repositories.component';
import {LocalesComponent} from './component/locales/locales.component';
import {LocaleViewCardComponent} from './component/locales/locale-view-card/locale-view-card.component';
import { RepositoryViewCardComponent } from './component/repositories/repository-view-card/repository-view-card.component';

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
        UserTableComponent,
        UserTableDetailsComponent,
        UsersComponent,
        RepositoriesComponent,
        LocalesComponent,
        LocaleViewCardComponent,
        RepositoryViewCardComponent
    ],
    entryComponents: [
        ConfirmWorkspaceDeletionComponent
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
