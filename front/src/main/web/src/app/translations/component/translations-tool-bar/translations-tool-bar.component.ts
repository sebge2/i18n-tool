import { Component, Input } from '@angular/core';
import { TranslationsTableState } from '../../model/search/translation-search-state.model';

@Component({
  selector: 'app-translations-tool-bar',
  templateUrl: './translations-tool-bar.component.html',
  styleUrls: ['./translations-tool-bar.component.css'],
})
export class TranslationsToolBarComponent {
  @Input() state: TranslationsTableState = new TranslationsTableState();

  constructor() {}

  onPreviousPage() {
    this.state.goOnPreviousPage();
  }

  onNextPage() {
    this.state.goOnNextPage();
  }
}
