import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-form-send-button',
  templateUrl: './form-send-button.component.html',
})
export class FormSendButtonComponent {
  @Input() public form: FormGroup;
  @Input() public disabled: boolean;
  @Input() public sendInProgress: boolean;
  @Output() public send = new EventEmitter<void>();

  constructor() {}

  ngOnInit() {}

  public onSend() {
    this.send.emit();
  }
}
