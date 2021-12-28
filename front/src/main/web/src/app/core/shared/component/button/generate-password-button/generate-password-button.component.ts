import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ClipboardService} from 'ngx-clipboard';

@Component({
    selector: 'app-generate-password-button',
    templateUrl: './generate-password-button.component.html',
})
export class GeneratePasswordButtonComponent {
    @Input() public disabled: boolean = false;
    @Input() public length: number = 6;
    @Output() public generatedPassword = new EventEmitter<string>();

    constructor(private _clipboardService: ClipboardService) {
    }

    public onClick() {
        if (this.disabled) {
            return;
        }

        const password = GeneratePasswordButtonComponent.generate(this.length);

        this._clipboardService.copy(password);

        this.generatedPassword.emit(password);
    }

    private static generate(length: number): string {
        let result = '';
        const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        const charactersLength = characters.length;

        for (let i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }

        return result;
    }
}
