import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-row-actions',
  templateUrl: './row-actions.component.html',
  styleUrls: ['./row-actions.component.scss']
})
export class RowActionsComponent {

  @Input() public actionsVisible : boolean = true;

  constructor() { }
}
