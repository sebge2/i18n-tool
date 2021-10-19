import {HttpResponse} from "@angular/common/http";

export class HttpUtils {

    static getContentDispositionFileName(response: HttpResponse<any>): string {
        const contentDisposition = response.headers.get('content-disposition');

        return contentDisposition.split(';')[1].split('filename')[1].split('=')[1].trim();
    }

}