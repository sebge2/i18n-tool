import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-sync-button',
  templateUrl: './sync-button.component.html',
  styleUrls: ['./sync-button.component.css']
})
export class SyncButtonComponent {

  @Input() public syncInProgress: boolean;
  @Input() public disabled: boolean;
  @Output() public sync = new EventEmitter<void>();

  constructor() {
  }

  public onSync() {
    this.sync.emit();
  }

}
