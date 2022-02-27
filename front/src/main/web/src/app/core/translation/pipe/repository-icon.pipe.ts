import { Pipe, PipeTransform } from '@angular/core';
import { RepositoryType } from '@i18n-core-translation';

@Pipe({
  name: 'repositoryIcon',
})
export class RepositoryIconPipe implements PipeTransform {
  transform(repositoryType: RepositoryType): string {
    return repositoryType ? `app-icon-${repositoryType.toLowerCase()}` : null;
  }
}
