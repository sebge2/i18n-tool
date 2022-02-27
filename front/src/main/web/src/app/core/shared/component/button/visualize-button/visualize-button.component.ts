import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-visualize-button',
  templateUrl: './visualize-button.component.html',
})
export class VisualizeButtonComponent {
  @Input() public disabled: boolean;
  @Output() public click = new EventEmitter<void>();

  constructor() {}

  public onClick() {
    this.click.emit();
  }
}
