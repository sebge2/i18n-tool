import { Component, Input } from '@angular/core';
import { Repository } from '@i18n-core-translation';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css'],
})
export class RepositoryDetailsComponent {
  @Input() public repository: Repository;

  constructor() {}
}
