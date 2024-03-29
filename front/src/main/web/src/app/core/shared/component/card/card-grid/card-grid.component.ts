import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-card-grid',
  templateUrl: './card-grid.component.html',
})
export class CardGridComponent implements OnInit {
  @Input() public allowAdd = true;
  @Input() public layout = 'flex-start';

  @Output() public add = new EventEmitter<void>();

  constructor() {}

  ngOnInit() {}

  public onAdd() {
    this.add.emit();
  }
}
