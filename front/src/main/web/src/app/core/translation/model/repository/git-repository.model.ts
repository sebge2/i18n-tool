import { Repository } from './repository.model';
import { GitRepositoryDto } from '../../../../api';
import { RepositoryType } from './repository-type.model';
import { RepositoryStatus } from './repository-status.model';
import { TranslationsConfiguration } from './translations-configuration.model';
import { BaseGitRepository } from './base-git-repository.model';

export class GitRepository extends BaseGitRepository {
  static fromDto(dto: GitRepositoryDto): Repository {
    return new GitRepository(
      dto.id,
      dto.name,
      RepositoryType[dto.type],
      RepositoryStatus[dto.status],
      TranslationsConfiguration.fromDto(dto.translationsConfiguration),
      dto.autoSynchronized,
      dto.location,
      dto.defaultBranch,
      dto.allowedBranches
    );
  }

  constructor(
    id: string,
    name: string,
    type: RepositoryType,
    status: RepositoryStatus,
    translationsConfiguration: TranslationsConfiguration,
    autoSynchronized: boolean,
    location: string,
    defaultBranch: string,
    allowedBranches: string
  ) {
    super(
      id,
      name,
      type,
      status,
      translationsConfiguration,
      location,
      defaultBranch,
      allowedBranches,
      autoSynchronized
    );
  }
}
