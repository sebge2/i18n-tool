import { Component, Input } from '@angular/core';
import { DictionaryTableState } from '../../../model/dictionary-table-state.model';

@Component({
  selector: 'app-dictionary-tool-bar',
  templateUrl: './dictionary-tool-bar.component.html',
  styleUrls: ['./dictionary-tool-bar.component.scss'],
})
export class DictionaryToolBarComponent {
  @Input() public state: DictionaryTableState = new DictionaryTableState();

  constructor() {}
}
