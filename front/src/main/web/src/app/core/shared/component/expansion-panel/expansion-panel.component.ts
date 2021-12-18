import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-expansion-panel',
  templateUrl: './expansion-panel.component.html',
  styleUrls: ['./expansion-panel.component.css'],
})
export class ExpansionPanelComponent {
  @Input() public expanded: boolean = false;
  @Output() public expandedChange = new EventEmitter<boolean>();

  constructor() {}

  public onExpandedChange(expanded: boolean) {
    this.expandedChange.emit(expanded);
  }
}
