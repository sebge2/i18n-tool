import { Component, Input } from '@angular/core';
import { User } from '@i18n-core-auth';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { ImportedFile } from '@i18n-core-shared';
import { FileExtension, IMAGE_FILE_EXTENSIONS } from '@i18n-core-shared';

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
  @Input() currentUser: User;

  disabled: boolean = false;
  allowedFileExtensions: FileExtension[] = IMAGE_FILE_EXTENSIONS;

  private _value: ImportedFile;
  private _valueUrl: string;

  constructor() {}

  get valueUrl(): string {
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
      this._doSetValue(value);

      this.onChange(value);
    }
  }

  onChange = (_) => {};

  onTouched = () => {};

  writeValue(value: ImportedFile): void {
    this._doSetValue(value);
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(disabled: boolean) {
    this.disabled = disabled;
  }

  onFile(file: ImportedFile) {
    this.value = file;
  }

  private _doSetValue(value: ImportedFile) {
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
