import {Directive, EventEmitter, HostListener, Output} from '@angular/core';
import * as _ from "lodash";

@Directive({
  selector: '[appInputTextSelectionListener]'
})
export class InputTextSelectionListenerDirective {

  @Output() readonly selectedText = new EventEmitter<string>();

  private _selection: string;

  constructor() { }

  @HostListener('click', ['$event.target'])
  @HostListener('keydown', ['$event.target'])
  @HostListener('blur', ['$event.target'])
  @HostListener('select', ['$event.target'])
  onEvent(input: HTMLInputElement): void {
    this._handleSelectionChange(input);
  }

  private _handleSelectionChange(input: HTMLInputElement) {
    const start = input.selectionStart;
    const end = input.selectionEnd;

    const selection = (!(_.isNil(start) || _.isNil(end)))
        ? input.value.substring(start, end)
        : null;

    if (!_.eq(this._selection, selection)) {
      this._selection = selection;

      this.selectedText.emit(selection);
    }
  }
}
