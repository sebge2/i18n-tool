import {Component, Input} from '@angular/core';
import {User} from "../../../../../core/auth/model/user.model";
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {ImportedFile} from "../../../../../core/shared/model/imported-file.model";
import {FileExtension, IMAGE_FILE_EXTENSIONS} from "../../../../../core/shared/model/file-extension.model";

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

    public disabled: boolean = false;
    public allowedFileExtensions: FileExtension[] = IMAGE_FILE_EXTENSIONS;
    private _value: ImportedFile;
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

    get value(): ImportedFile {
        return this._value;
    }

    set value(value: ImportedFile) {
        if (value !== this._value) {
            this.doSetValue(value);

            this.onChange(value);
        }
    }

    public onChange = (_) => {
    };

    public onTouched = () => {
    };

    public writeValue(value: ImportedFile): void {
        this.doSetValue(value);
    }

    public registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    public registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState(disabled: boolean) {
        this.disabled = disabled;
    }

    public onFileDropped(file: ImportedFile) {
        this.value = file;
    }

    public onClick() {

    }

    private doSetValue(value: ImportedFile) {
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
