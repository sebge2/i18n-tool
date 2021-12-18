import { ImportedFile } from './imported-file.model';

export enum FileExtension {
  JPG = 'jpg',

  JPEG = 'jpeg',

  PNG = 'png',

  ZIP = 'zip',

  CSV = 'csv',
}

export const IMAGE_FILE_EXTENSIONS: FileExtension[] = [FileExtension.JPG, FileExtension.JPEG, FileExtension.PNG];

export let FILE_CONTENT_TYPES = new Map([
  [FileExtension.JPG, 'image/jpeg'],
  [FileExtension.JPEG, 'image/jpeg'],
  [FileExtension.PNG, 'image/png'],
  [FileExtension.ZIP, 'application/zip'],
  [FileExtension.CSV, 'text/csv'],
]);

export function createImportedFile(file: File, allowedFileExtensions: FileExtension[]): ImportedFile {
  let contentType = file.type;

  for (const extension of allowedFileExtensions) {
    if (file.name.toLowerCase().endsWith(`.${extension}`)) {
      contentType = FILE_CONTENT_TYPES.get(extension);
      return new ImportedFile(file, contentType);
    }
  }

  return null;
}
