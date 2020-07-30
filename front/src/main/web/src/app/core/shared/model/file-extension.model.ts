export enum FileExtension {

    JPG = 'JPG',

    JPEG = 'JPEG',

    PNG = 'PNG'

}

export let FILE_CONTENT_TYPES = new Map([
    [FileExtension.JPG, 'image/jpeg'],
    [FileExtension.JPEG, 'image/jpeg'],
    [FileExtension.PNG, 'image/png'],
]);
