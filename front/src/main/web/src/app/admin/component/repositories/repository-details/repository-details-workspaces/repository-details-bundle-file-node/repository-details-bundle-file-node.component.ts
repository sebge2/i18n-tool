import { Component, Input } from '@angular/core';
import { WorkspaceBundleTreeNode } from '../repository-details-workspaces.component';
import { GitHubFileLink, GitHubLink } from '@i18n-core-shared';
import { RepositoryType } from '@i18n-core-translation';
import { GitHubRepository } from '@i18n-core-translation';
import { Router } from '@angular/router';
import { Repository } from '@i18n-core-translation';
import { RepositoryService } from '@i18n-core-translation';
import { RepositoryPatchRequestDto, TranslationsConfigurationPatchDto } from '../../../../../../api';
import { BundleType } from '@i18n-core-translation';
import * as _ from 'lodash';
import { NotificationService } from '@i18n-core-notification';

@Component({
  selector: 'app-repository-details-bundle-file-node',
  templateUrl: './repository-details-bundle-file-node.component.html',
  styleUrls: ['./repository-details-bundle-file-node.component.css'],
})
export class RepositoryDetailsBundleFileNodeComponent {
  @Input() public node: WorkspaceBundleTreeNode;

  public banInProgress: boolean;

  constructor(
    private _router: Router,
    private _repositoryService: RepositoryService,
    private _notificationService: NotificationService
  ) {}

  public get bundleLink(): GitHubLink {
    if (this.node && this.node.repository.type === RepositoryType.GITHUB) {
      const repository = <GitHubRepository>this.node.repository;

      return new GitHubFileLink(
        repository.username,
        repository.repository,
        this.node.workspace.branch,
        this.node.bundleFile.location
      );
    } else {
      return null;
    }
  }

  public onSearchTranslations(): Promise<any> {
    return this._router.navigate(['/translations'], {
      queryParams: { workspace: this.node.workspace.id, bundleFile: this.node.bundleFile.id },
    });
  }

  public onBan() {
    const repository = <Repository>this.node.repository;
    const bundleFile = this.node.bundleFile;

    this.banInProgress = true;

    let translationsConfiguration: TranslationsConfigurationPatchDto;
    switch (bundleFile.type) {
      case BundleType.JAVA_PROPERTIES:
        translationsConfiguration = {
          javaProperties: {
            ignoredPaths: _.concat(
              repository.translationsConfiguration.javaPropertiesConfiguration.ignoredPaths,
              bundleFile.locationPathPattern
            ),
          },
        };
        break;
      case BundleType.JSON_ICU:
        translationsConfiguration = {
          jsonIcu: {
            ignoredPaths: _.concat(
              repository.translationsConfiguration.javaPropertiesConfiguration.ignoredPaths,
              bundleFile.locationPathPattern
            ),
          },
        };
        break;
      default:
        throw Error(`Unsupported bundle type [${bundleFile.type}].`);
    }

    this._repositoryService
      .updateRepository(repository.id, <RepositoryPatchRequestDto>{
        id: repository.id,
        type: repository.type,
        translationsConfiguration: translationsConfiguration,
      })
      .toPromise()
      .then(() => this._notificationService.displayInfoMessage('ADMIN.WORKSPACES.EXPLORE.BUNDLE_FILE_EXCLUDED'))
      .catch((error) =>
        this._notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.EXCLUDING_BUNDLE_FILE', error)
      )
      .finally(() => (this.banInProgress = false));
  }
}
