import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FileExtension } from '../../model/file-extension.model';
import { ImportedFile } from '../../model/imported-file.model';

@Component({
  selector: 'app-upload-zone',
  templateUrl: './upload-zone.component.html',
  styleUrls: ['./upload-zone.component.scss'],
})
export class UploadZoneComponent {
  @Input() public allowedFileExtensions: FileExtension[] = [];
  @Input() public disabled: boolean = false;
  @Input() public backgroundImage: string;
  @Output() public file = new EventEmitter<ImportedFile>();

  constructor() {}

  public onFileDropped(file: ImportedFile): void {
    this.file.emit(file);
  }

  public onFileImported(file: ImportedFile): void {
    this.file.emit(file);
  }
}
