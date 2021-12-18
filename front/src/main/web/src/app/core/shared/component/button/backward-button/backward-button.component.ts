import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-backward-button',
  templateUrl: './backward-button.component.html',
})
export class BackwardButtonComponent {
  @Input() public disabled: boolean = false;
  @Output() public publish = new EventEmitter<void>();

  constructor() {}

  public onClick() {
    this.publish.emit(null);
  }
}
