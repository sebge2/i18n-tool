import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AppVersion} from "../model/app-version.model";
import {map} from "rxjs/operators";

interface BuildDto {
    version: string;
    time: Date;
}

interface InfoDto {
    build: BuildDto;
}

@Injectable({
    providedIn: 'root'
})
export class AppVersionService {

    constructor(private _httpClient: HttpClient) {
    }

    get version(): Observable<AppVersion> {
        return this._httpClient
            .get<InfoDto>('/api/info')
            .pipe(map(dto => new AppVersion(dto.build.version, dto.build.time)));
    }
}
