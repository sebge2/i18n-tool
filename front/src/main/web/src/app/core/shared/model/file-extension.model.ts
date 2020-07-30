export enum FileExtension {

    JPG = 'jpg',

    JPEG = 'jpeg',

    PNG = 'png'

}

export const IMAGE_FILE_EXTENSIONS: FileExtension[] = [FileExtension.JPG, FileExtension.JPEG, FileExtension.PNG];

export let FILE_CONTENT_TYPES = new Map([
    [FileExtension.JPG, 'image/jpeg'],
    [FileExtension.JPEG, 'image/jpeg'],
    [FileExtension.PNG, 'image/png'],
]);
