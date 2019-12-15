import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-form-search-button',
  templateUrl: './form-search-button.component.html',
  styleUrls: ['./form-search-button.component.css']
})
export class FormSearchButtonComponent {

  @Input() public form: FormGroup;
  @Input() public disabled: boolean;
  @Input() public searchInProgress: boolean;
  @Output() public search = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit() {
  }

  public onSearch() {
    this.search.emit();
  }
}
