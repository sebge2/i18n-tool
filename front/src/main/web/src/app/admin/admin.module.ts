import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import {AdminComponent} from './component/admin/admin.component';
import {WorkspaceTableComponent} from './component/workspace-table/workspace-table.component';
import {MaterialModule} from "../core/ui/material.module";
import {FlexLayoutModule} from "@angular/flex-layout";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RepositoryInitializerComponent} from './component/repository-initializer/repository-initializer.component';
import {CoreSharedModule} from "../core/shared/core-shared-module";
import {ConfirmWorkspaceDeletionComponent} from './component/workspace-table/confirm-deletion/confirm-workspace-deletion.component';

const appRoutes: Routes = [
    {path: '', pathMatch: 'full', component: AdminComponent}
];

@NgModule({
    declarations: [
        AdminComponent,
        WorkspaceTableComponent,
        RepositoryInitializerComponent,
        ConfirmWorkspaceDeletionComponent
    ],
    entryComponents: [
        ConfirmWorkspaceDeletionComponent
    ],
    imports: [
        CommonModule,
        CoreSharedModule,

        RouterModule.forChild(appRoutes),

        MaterialModule,
        ReactiveFormsModule,
        FormsModule,
        FlexLayoutModule
    ],
    exports: [RouterModule]
})
export class AdminModule {
}
