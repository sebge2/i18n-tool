import { Pipe, PipeTransform } from '@angular/core';
import {Repository} from "../../../translations/model/repository/repository.model";
import {RepositoryType} from "../../../translations/model/repository/repository-type.model";

@Pipe({
  name: 'repositoryIcon'
})
export class RepositoryIconPipe implements PipeTransform {

  transform(repositoryType: RepositoryType): string {
    return repositoryType ? `app-icon-${repositoryType.toLowerCase()}` : null;
  }

}
