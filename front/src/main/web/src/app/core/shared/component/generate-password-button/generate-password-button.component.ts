import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ClipboardService} from "ngx-clipboard";
import {NotificationService} from "../../../notification/service/notification.service";

@Component({
    selector: 'app-generate-password-button',
    templateUrl: './generate-password-button.component.html',
    styleUrls: ['./generate-password-button.component.css']
})
export class GeneratePasswordButtonComponent implements OnInit {

    @Input() public disabled: boolean = false;
    @Input() public length: number = 6;
    @Input() public notificationMessage: string;
    @Output() public generatedPassword = new EventEmitter<string>();

    constructor(private _clipboardService: ClipboardService,
                private _notificationService: NotificationService) {
    }

    ngOnInit(): void {
    }

    public onClick() {
        const password = GeneratePasswordButtonComponent.generate(this.length);

        this._clipboardService.copy(password);

        if (this.notificationMessage) {
            this._notificationService.displayInfoMessage(this.notificationMessage);
        }

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
