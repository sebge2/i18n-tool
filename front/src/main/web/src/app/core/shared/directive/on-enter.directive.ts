import { Directive, EventEmitter, HostListener, Output } from '@angular/core';

@Directive({
  selector: '[onEnter]',
})
export class OnEnterDirective {
  @Output('onEnter') onEnter: EventEmitter<KeyboardEvent> = new EventEmitter();

  constructor() {}

  @HostListener('keydown.enter', ['$event'])
  public onSubmit(event: KeyboardEvent) {
    event.preventDefault();
    this.onEnter.emit(event);
  }
}
