import {Injectable} from '@angular/core';
import packageInfo from "../../../../../package.json";

@Injectable({
    providedIn: 'root'
})
export class AppVersionService {

    constructor() {
    }

    get version(): string {
        return packageInfo.version;
    }
}
