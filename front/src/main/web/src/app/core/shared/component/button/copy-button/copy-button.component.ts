import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ClipboardService} from "ngx-clipboard";
import {SemanticColor, SEMANTIC_COLOR_THEME} from "@i18n-core-shared";
import {BUTTON_SIZE_CLASS, ButtonSize} from '../../../model/button/button-size.enum';

@Component({
    selector: 'app-copy-button',
    templateUrl: './copy-button.component.html',
})
export class CopyButtonComponent {

    @Input() disabled: boolean = false;
    @Input() color: SemanticColor | undefined = SemanticColor.PRIMARY;
    @Input() size: ButtonSize = ButtonSize.NORMAL;
    @Input() text: string;

    @Output() click = new EventEmitter<string>();

    BUTTON_SIZE_CLASS = BUTTON_SIZE_CLASS;
    SEMANTIC_COLOR_THEME = SEMANTIC_COLOR_THEME;

    constructor(private _clipboardService: ClipboardService) {
    }

    onClick() {
        if (this.disabled) {
            return;
        }

        this._clipboardService.copy(this.text);

        this.click.emit(this.text);
    }
}
