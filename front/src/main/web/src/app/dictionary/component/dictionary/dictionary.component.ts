import { Component } from '@angular/core';

import { TranslationsTableState } from '../../../translations/model/search/translation-search-state.model';

@Component({
  selector: 'app-dictionary',
  templateUrl: './dictionary.component.html',
  styleUrls: ['./dictionary.component.scss'],
})
export class DictionaryComponent {
  public readonly tableState = new TranslationsTableState();

  constructor() {}
}
