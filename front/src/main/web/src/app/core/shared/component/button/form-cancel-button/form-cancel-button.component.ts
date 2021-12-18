import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-form-cancel-button',
  templateUrl: './form-cancel-button.component.html',
})
export class FormCancelButtonComponent {
  @Input() public form: FormGroup;
  @Input() public disabled: boolean;
  @Input() public cancelInProgress: boolean;
  @Output() public reset = new EventEmitter<void>();

  constructor() {}

  public onReset() {
    this.reset.emit();
  }
}
