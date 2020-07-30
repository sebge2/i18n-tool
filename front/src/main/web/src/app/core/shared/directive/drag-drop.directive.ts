import {Directive, EventEmitter, HostBinding, HostListener, Input, Output} from '@angular/core';
import {FILE_CONTENT_TYPES, FileExtension} from "../model/file-extension.model";
import {ImportedFile} from "../model/imported-file.model";

@Directive({
    selector: '[appDragDrop]'
})
export class DragDropDirective {

    @Input() public allowedFileExtensions: FileExtension[] = [];
    @Output() public onFileDropped = new EventEmitter<ImportedFile>();

    @HostBinding('class.dragAndDropZone') private dragAndDropZoneClass = true;
    @HostBinding('class.dragAndDropZoneOver') private dragAndDropZoneOverClass = false;
    @HostBinding('class.dragAndDropZoneNotAllowed') private dragAndDropZoneNotAllowed = false;

    private _disabled: boolean;

    constructor() {
    }

    @Input()
    public get disabled(): boolean {
        return this._disabled;
    }

    public set disabled(disabled: boolean) {
        this._disabled = disabled;
        this.dragAndDropZoneNotAllowed = disabled;
    }

    @HostListener('dragover', ['$event'])
    public onDragOver(evt) {
        evt.preventDefault();
        evt.stopPropagation();

        if (!this.disabled) {
            this.dragAndDropZoneOverClass = true;
        }
    }

    @HostListener('dragleave', ['$event'])
    public onDragLeave(evt) {
        evt.preventDefault();
        evt.stopPropagation();

        if (!this.disabled) {
            this.dragAndDropZoneOverClass = false;
        }
    }

    @HostListener('drop', ['$event'])
    public ondrop(evt) {
        evt.preventDefault();
        evt.stopPropagation();

        if (!this.disabled) {
            this.dragAndDropZoneOverClass = false;

            const dataTransfer: DataTransfer = evt.dataTransfer;

            if (evt.dataTransfer.files.length == 1) {
                const file = this.createDroppedFile(dataTransfer.files[0]);

                if (file) {
                    this.onFileDropped.emit(file)
                }
            }
        }
    }

    private createDroppedFile(file: File): ImportedFile {
        let contentType = file.type;

        for (const extension of this.allowedFileExtensions) {
            if (file.name.toLowerCase().endsWith(`.${extension}`)) {
                contentType = FILE_CONTENT_TYPES.get(extension)
                return new ImportedFile(file, contentType);
            }
        }

        return null;
    }
}
