import { RepositoryStatus } from './repository-status.model';
import { RepositoryType } from './repository-type.model';
import { TranslationsConfiguration } from './translations-configuration.model';

export class Repository {
  constructor(
    public id: string,
    public name: string,
    public type: RepositoryType,
    public status: RepositoryStatus,
    public translationsConfiguration: TranslationsConfiguration,
    public autoSynchronized: boolean
  ) {}

  isNotInitialized(): boolean {
    return this.status == RepositoryStatus.NOT_INITIALIZED;
  }

  isInitializing(): boolean {
    return this.status == RepositoryStatus.INITIALIZING;
  }

  isInitialized(): boolean {
    return this.status == RepositoryStatus.INITIALIZED;
  }
}
