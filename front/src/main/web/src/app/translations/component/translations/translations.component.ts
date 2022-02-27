import { Component } from '@angular/core';
import { TranslationsSearchRequest } from '../../model/search/translations-search-request.model';
import { TranslationsPage } from '../../model/search/translations-page.model';
import { TranslationsTableState } from '../../model/search/translation-search-state.model';

@Component({
  selector: 'app-translations',
  templateUrl: './translations.component.html',
  styleUrls: ['./translations.component.scss'],
})
export class TranslationsComponent {
  expanded: boolean = true;
  readonly tableState = new TranslationsTableState();

  page: TranslationsPage;

  constructor() {}

  onSearch(searchRequest: TranslationsSearchRequest) {
    this.expanded = false;
    setTimeout(() => this.tableState.notifyNewSearchRequest(searchRequest), 300);
  }

  onPage(page: TranslationsPage) {
    this.page = page;
  }
}
