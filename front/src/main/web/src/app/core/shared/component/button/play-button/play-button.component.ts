import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-play-button',
  templateUrl: './play-button.component.html',
})
export class PlayButtonComponent {
  @Input() state = PlayButtonState.STOPPED;
  @Input() public actionInProgress: boolean;
  @Input() public disabled: boolean;
  @Output() public press = new EventEmitter<void>();

  constructor() {}

  public get icon(): string {
    return PlayButtonStateIcon[this.state];
  }

  public get iconCls(): string {
    return PlayButtonStateIconCls[this.state];
  }

  public onClick() {
    this.press.emit();
  }
}

export enum PlayButtonState {
  STOPPED = 'STOPPED',

  STARTED = 'STARTED',
}

export const PlayButtonStateIcon = {
  [PlayButtonState.STOPPED]: `play_arrow`,
  [PlayButtonState.STARTED]: `stop`,
};

export const PlayButtonStateIconCls = {
  [PlayButtonState.STOPPED]: `status-success`,
  [PlayButtonState.STARTED]: `status-danger`,
};
