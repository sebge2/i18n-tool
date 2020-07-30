import {Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {ThemePalette} from "@angular/material/core/typings/common-behaviors/color";
import {createImportedFile, FileExtension} from "../../model/file-extension.model";
import {ImportedFile} from "../../model/imported-file.model";
import {MouseEventUtils} from "../../utils/mouse-event-utils";
import * as _ from "lodash";

@Component({
    selector: 'app-form-upload-button',
    templateUrl: './form-upload-button.component.html',
    styleUrls: ['./form-upload-button.component.css']
})
export class FormUploadButtonComponent {

    @Input() public allowedFileExtensions: FileExtension[] = [];
    @Input() public color: ThemePalette;
    @Input() public class: string;
    @Input() public disabled: boolean = false;
    @Output() public import = new EventEmitter<ImportedFile>();

    @ViewChild('fileInput', {static: true}) fileInput: ElementRef<HTMLInputElement>;

    constructor() {
    }

    public onItemClick(event: MouseEvent) {
        MouseEventUtils.stopPropagation(event);
        this.fileInput.nativeElement.click();

        this.onFilesImport = (files: FileList) => {
            const file = files.item(0);
            const importedFile = createImportedFile(file, this.allowedFileExtensions);

            if (!importedFile) {
                console.error("Cannot import file.", file);
            } else {
                this.import.emit(importedFile);
                this.fileInput.nativeElement.value = null;
            }
        };
    }

    public onInputClick(event: MouseEvent) {
        MouseEventUtils.stopPropagation(event);
    }

    public onFilesImport = (files: FileList) => {
    };

    public getAcceptTypes(): string {
        return _.map(this.allowedFileExtensions, (t) => `.${t}`).join(',');
    }
}
