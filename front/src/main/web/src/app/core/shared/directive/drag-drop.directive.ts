import { Directive, EventEmitter, HostBinding, HostListener, Input, Output } from '@angular/core';
import { createImportedFile, FileExtension } from '../model/file-extension.model';
import { ImportedFile } from '../model/imported-file.model';

@Directive({
  selector: '[appDragDrop]',
})
export class DragDropDirective {
  @Input() public allowedFileExtensions: FileExtension[] = [];
  @Output() public onFileDropped = new EventEmitter<ImportedFile>();

  @HostBinding('class.dragAndDropZone') private dragAndDropZoneClass = true;
  @HostBinding('class.dragAndDropZoneOver') private dragAndDropZoneOverClass = false;
  @HostBinding('class.dragAndDropZoneNotAllowed') private dragAndDropZoneNotAllowed = false;

  private _disabled: boolean;

  constructor() {}

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
        const file = createImportedFile(dataTransfer.files[0], this.allowedFileExtensions);

        if (file) {
          this.onFileDropped.emit(file);
        }
      }
    }
  }
}
