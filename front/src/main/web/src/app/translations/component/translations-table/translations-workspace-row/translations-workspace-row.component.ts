import { Component, Input } from '@angular/core';
import { Workspace } from '@i18n-core-translation';

@Component({
  selector: 'app-translations-workspace-row',
  templateUrl: './translations-workspace-row.component.html',
  styleUrls: ['./translations-workspace-row.component.css'],
})
export class TranslationsWorkspaceRowComponent {
  @Input() workspace: Workspace;

  constructor() {}
}
