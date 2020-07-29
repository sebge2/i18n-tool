import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ThemePalette} from "@angular/material/core/typings/common-behaviors/color";

@Component({
    selector: 'app-form-upload-button',
    templateUrl: './form-upload-button.component.html',
    styleUrls: ['./form-upload-button.component.css']
})
export class FormUploadButtonComponent {

    @Input() public color: ThemePalette;
    @Input() public class: string;
    @Input() public disabled: boolean = false;
    @Output() public click = new EventEmitter<void>();

    constructor() {
    }

    public onClick() {
        this.click.emit();
    }
}
