import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-form-apply-button',
  templateUrl: './form-apply-button.component.html',
})
export class FormApplyButtonComponent {
  @Input() public form: FormGroup;
  @Input() public disabled: boolean;
  @Input() public applyInProgress: boolean;
  @Output() public apply = new EventEmitter<void>();

  constructor() {}

  public onApply() {
    this.apply.emit();
  }
}
