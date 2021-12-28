import { Component, Input } from '@angular/core';
import { DictionaryTableState } from '../../../model/dictionary-table-state.model';

@Component({
  selector: 'app-dictionary-bottom-bar',
  templateUrl: './dictionary-bottom-bar.component.html',
  styleUrls: ['./dictionary-bottom-bar.component.scss'],
})
export class DictionaryBottomBarComponent {
  @Input() public state: DictionaryTableState = new DictionaryTableState();

  constructor() {}
}
