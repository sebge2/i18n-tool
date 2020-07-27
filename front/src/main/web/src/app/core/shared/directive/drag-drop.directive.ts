import {Directive, EventEmitter, HostBinding, HostListener, Output} from '@angular/core';

export class DroppedFile {

    constructor(public file: File, public contentType: string) {
    }
}

export let FILE_TYPES = new Map([
    ['jpg', 'image/jpeg'],
    ['jpeg', 'image/jpeg'],
    ['png', 'image/png'],
]);

@Directive({
    selector: '[appDragDrop]'
})
export class DragDropDirective {

    @Output() onFileDropped = new EventEmitter<DroppedFile>();

    @HostBinding('class.dragAndDropZone') private dragAndDropZoneClass = true;
    @HostBinding('class.dragAndDropZoneOver') private dragAndDropZoneOverClass = false;

    @HostListener('dragover', ['$event']) onDragOver(evt) {
        evt.preventDefault();
        evt.stopPropagation();

        this.dragAndDropZoneOverClass = true;
    }

    @HostListener('dragleave', ['$event'])
    public onDragLeave(evt) {
        evt.preventDefault();
        evt.stopPropagation();

        this.dragAndDropZoneOverClass = false;
    }

    @HostListener('drop', ['$event'])
    public ondrop(evt) {
        evt.preventDefault();
        evt.stopPropagation();

        this.dragAndDropZoneOverClass = false;

        const dataTransfer: DataTransfer = evt.dataTransfer;

        if (evt.dataTransfer.files.length == 1) {
            this.onFileDropped.emit(this.createDroppedFile(dataTransfer.files[0]))
        }
    }

    private createDroppedFile(file: File): DroppedFile {
        let contentType = file.type;

        for (const extension of FILE_TYPES.keys()) {
            if (file.name.toLowerCase().endsWith(`.${extension}`)) {
                contentType = FILE_TYPES.get(extension)
                break;
            }
        }

        return new DroppedFile(file, contentType);
    }
}
