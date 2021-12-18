import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-download-button',
  templateUrl: './download-button.component.html',
})
export class DownloadButtonComponent {
  @Input() public initInProgress: boolean;
  @Input() public disabled: boolean;
  @Input() public buttonClass: string = 'normal';
  @Output() public download = new EventEmitter<void>();

  constructor() {}

  public onClick() {
    this.download.emit();
  }
}
