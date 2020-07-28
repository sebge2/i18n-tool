import {Component, Input} from '@angular/core';
import {User} from "../../../../../core/auth/model/user.model";
import {DroppedFile} from "../../../../../core/shared/directive/drag-drop.directive";
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";

export interface AvatarFile {
    file: File;
    contentType: string;
}

@Component({
    selector: 'app-edit-profile-avatar',
    templateUrl: './edit-profile-avatar.component.html',
    styleUrls: ['./edit-profile-avatar.component.css'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: EditProfileAvatarComponent,
            multi: true,
        },
    ],
})
export class EditProfileAvatarComponent implements ControlValueAccessor {

    @Input() public currentUser: User;

    private _value: AvatarFile;
    private _valueUrl: string;

    constructor() {
    }

    public get valueUrl(): string {
        if (this._valueUrl) {
            return this._valueUrl;
        } else if (this.currentUser) {
            return `url('/api/user/${this.currentUser.id}/avatar')`;
        } else {
            return null;
        }
    }

    get value(): AvatarFile {
        return this._value;
    }

    set value(value: AvatarFile) {
        if (value !== this._value) {
            this.doSetValue(value);

            this.onChange(value);
        }
    }

    public onChange = (_) => {
    };

    public onTouched = () => {
    };

    public writeValue(value: AvatarFile): void {
        this.doSetValue(value);
    }

    public registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    public registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    public onFileDropped(file: DroppedFile) {
        this.value = {file: file.file, contentType: file.contentType};
    }

    public onClick() {

    }

    private doSetValue(value: AvatarFile) {
        this._value = value;

        if (value) {
            const reader = new FileReader();
            reader.readAsDataURL(value.file);
            reader.onload = (_event) => {
                this._valueUrl = `url(${reader.result})`;
            };
        } else {
            this._valueUrl = null;
        }
    }
}
