import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CoreSharedModule } from '@i18n-core-shared';
import { CoreAuthModule } from '@i18n-core-auth';
import { UsersComponent } from './component/users/users.component';
import { RepositoriesComponent } from './component/repositories/repositories.component';
import { LocalesComponent } from './component/locales/locales.component';
import { LocaleViewCardComponent } from './component/locales/locale-view-card/locale-view-card.component';
import { RepositoryViewCardComponent } from './component/repositories/repository-list/repository-view-card/repository-view-card.component';
import { UserViewCardComponent } from './component/users/user-view-card/user-view-card.component';
import { RepositoryDetailsComponent } from './component/repositories/repository-details/repository-details.component';
import { RepositoryListComponent } from './component/repositories/repository-list/repository-list.component';
import { RepositoryAddWizardComponent } from './component/repositories/repository-list/repository-add-wizard/repository-add-wizard.component';
import { RepositoryAddWizardStepTypeComponent } from './component/repositories/repository-list/repository-add-wizard/repository-add-wizard-step-type/repository-add-wizard-step-type.component';
import { RepositoryAddWizardStepInfoComponent } from './component/repositories/repository-list/repository-add-wizard/repository-add-wizard-step-info/repository-add-wizard-step-info.component';
import { RepositoryAddWizardStepCreationComponent } from './component/repositories/repository-list/repository-add-wizard/repository-add-wizard-step-creation/repository-add-wizard-step-creation.component';
import { RepositoryAddWizardStepInitializationComponent } from './component/repositories/repository-list/repository-add-wizard/repository-add-wizard-step-initialization/repository-add-wizard-step-initialization.component';
import { RepositoryDetailsConfigComponent } from './component/repositories/repository-details/repository-details-config/repository-details-config.component';
import { RepositoryDetailsWorkspacesComponent } from './component/repositories/repository-details/repository-details-workspaces/repository-details-workspaces.component';
import { RepositoryDetailsWorkspaceNodeComponent } from './component/repositories/repository-details/repository-details-workspaces/repository-details-workspace-node/repository-details-workspace-node.component';
import { RepositoryDetailsBundleFileNodeComponent } from './component/repositories/repository-details/repository-details-workspaces/repository-details-bundle-file-node/repository-details-bundle-file-node.component';
import { RepositoryDetailsBundleFileEntryNodeComponent } from './component/repositories/repository-details/repository-details-workspaces/repository-details-bundle-file-entry-node/repository-details-bundle-file-entry-node.component';
import { RepositoryDetailsWorkspaceTreeNodeComponent } from './component/repositories/repository-details/repository-details-workspaces/repository-details-workspace-tree-node/repository-details-workspace-tree-node.component';
import { SafePipeModule } from 'safe-pipe';
import { CoreTranslationModule } from '@i18n-core-translation';
import { RepositoryGithubWebHookDialogComponent } from './component/repositories/repository-details/repository-details-config/repository-github-web-hook-dialog/repository-github-web-hook-dialog.component';
import { RepositoryGithubAccessKeyDialogComponent } from './component/repositories/repository-details/repository-details-config/repository-github-access-key-dialog/repository-github-access-key-dialog.component';
import { SnapshotsComponent } from './component/snapshot/snapshots/snapshots.component';
import { SnapshotCreationFormComponent } from './component/snapshot/snapshots/snapshot-creation-form/snapshot-creation-form.component';
import { SnapshotImportFormComponent } from './component/snapshot/snapshots/snapshot-import-form/snapshot-import-form.component';
import { RepositoryGitCredentialsDialogComponent } from './component/repositories/repository-details/repository-details-config/repository-git-credentials-dialog/repository-git-credentials-dialog.component';
import { RepositoryDetailsTranslationsConfigurationComponent } from './component/repositories/repository-details/repository-details-workspaces/repository-details-translations-configuration/repository-details-translations-configuration.component';
import { RepositoryDetailsTranslationsGlobalConfigurationComponent } from './component/repositories/repository-details/repository-details-workspaces/repository-details-translations-configuration/repository-details-translations-global-configuration/repository-details-translations-global-configuration.component';
import { RepositoryDetailsTranslationsBundleConfigurationComponent } from './component/repositories/repository-details/repository-details-workspaces/repository-details-translations-configuration/repository-details-translations-bundle-configuration/repository-details-translations-bundle-configuration.component';
import { ScheduledTasksComponent } from './component/scheduled-tasks/scheduled-tasks.component';
import { ScheduledTaskDefinitionListComponent } from './component/scheduled-tasks/scheduled-task-definition-list/scheduled-task-definition-list.component';
import { ScheduledTaskExecutionListComponent } from './component/scheduled-tasks/scheduled-task-execution-list/scheduled-task-execution-list.component';
import { ExternalTranslatorsComponent } from './component/external-translators/external-translators.component';
import { ExternalTranslatorAddPopupComponent } from './component/external-translators/external-translator-add-popup/external-translator-add-popup.component';
import { ExternalTranslatorAddPopupSelectTypeComponent } from './component/external-translators/external-translator-add-popup/external-translator-add-popup-select-type/external-translator-add-popup-select-type.component';
import { ExternalTranslatorAddPopupConfigureComponent } from './component/external-translators/external-translator-add-popup/external-translator-add-popup-configure/external-translator-add-popup-configure.component';
import { ExternalTranslatorAddPopupResultComponent } from './component/external-translators/external-translator-add-popup/external-translator-add-popup-result/external-translator-add-popup-result.component';

const appRoutes: Routes = [
  { path: 'users', component: UsersComponent },
  { path: 'repositories', component: RepositoriesComponent },
  { path: 'locales', component: LocalesComponent },
  { path: 'snapshots', component: SnapshotsComponent },
  { path: 'scheduled-tasks', component: ScheduledTasksComponent },
  { path: 'external-translators', component: ExternalTranslatorsComponent },
];

@NgModule({
  declarations: [
    LocalesComponent,
    LocaleViewCardComponent,

    UsersComponent,
    UserViewCardComponent,

    RepositoriesComponent,
    RepositoryListComponent,
    RepositoryViewCardComponent,
    RepositoryAddWizardComponent,
    RepositoryAddWizardStepTypeComponent,
    RepositoryAddWizardStepInfoComponent,
    RepositoryAddWizardStepCreationComponent,
    RepositoryAddWizardStepInitializationComponent,
    RepositoryDetailsComponent,
    RepositoryDetailsConfigComponent,
    RepositoryDetailsWorkspacesComponent,
    RepositoryDetailsWorkspaceNodeComponent,
    RepositoryDetailsBundleFileNodeComponent,
    RepositoryDetailsBundleFileEntryNodeComponent,
    RepositoryDetailsWorkspaceTreeNodeComponent,
    RepositoryDetailsTranslationsConfigurationComponent,
    RepositoryGithubWebHookDialogComponent,
    RepositoryGithubAccessKeyDialogComponent,
    RepositoryGitCredentialsDialogComponent,
    SnapshotsComponent,
    SnapshotCreationFormComponent,
    SnapshotImportFormComponent,
    RepositoryDetailsTranslationsGlobalConfigurationComponent,
    RepositoryDetailsTranslationsBundleConfigurationComponent,
    ScheduledTasksComponent,
    ScheduledTaskDefinitionListComponent,
    ScheduledTaskExecutionListComponent,
    ExternalTranslatorsComponent,
    ExternalTranslatorAddPopupComponent,
    ExternalTranslatorAddPopupSelectTypeComponent,
    ExternalTranslatorAddPopupConfigureComponent,
    ExternalTranslatorAddPopupResultComponent,
  ],
  imports: [
    CommonModule,
    CoreSharedModule,
    CoreAuthModule,
    CoreTranslationModule,

    RouterModule.forChild(appRoutes),

    SafePipeModule,
  ],
  exports: [RouterModule],
})
export class AdminModule {}
