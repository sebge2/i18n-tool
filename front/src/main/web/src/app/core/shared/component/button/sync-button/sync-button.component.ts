import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-sync-button',
  templateUrl: './sync-button.component.html',
})
export class SyncButtonComponent {
  @Input() public syncInProgress: boolean;
  @Input() public disabled: boolean;
  @Output() public sync = new EventEmitter<void>();

  constructor() {}

  public onSync() {
    this.sync.emit();
  }
}
