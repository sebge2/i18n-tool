import { Pipe, PipeTransform } from '@angular/core';
import {Repository} from "../../../translations/model/repository.model";

@Pipe({
  name: 'repositoryIcon'
})
export class RepositoryIconPipe implements PipeTransform {

  transform(repository: Repository): string {
    return repository ? `app-icon-${repository.type.toLowerCase()}` : null;
  }

}
