import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BUTTON_SIZE_CLASS, ButtonSize} from '../../../model/button/button-size.enum';
import {SemanticColor, SEMANTIC_COLOR_THEME} from '../../../model/semantic-color.enum';

@Component({
    selector: 'app-backward-button',
    templateUrl: './backward-button.component.html',
})
export class BackwardButtonComponent {
    @Input() disabled: boolean = false;
    @Input() color: SemanticColor | undefined = SemanticColor.PRIMARY;
    @Input() size: ButtonSize = ButtonSize.NORMAL;
    @Output() publish = new EventEmitter<void>();

    BUTTON_SIZE_CLASS = BUTTON_SIZE_CLASS;
    SEMANTIC_COLOR_THEME = SEMANTIC_COLOR_THEME;

    constructor() {
    }

    onClick() {
        this.publish.emit(null);
    }
}
