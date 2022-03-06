import {Component, OnDestroy, OnInit} from '@angular/core';
import {AppVersion, AppVersionService} from "@i18n-core-shared";
import {of, Subject} from "rxjs";
import {catchError, map, takeUntil} from "rxjs/operators";
import _ from "lodash";

interface AppInfo {
    version: string,
    link: string
}

@Component({
    selector: 'app-help-tool',
    templateUrl: 'help-tool.component.html',
})
export class HelpToolComponent implements OnInit, OnDestroy {

    appInfo: AppInfo;

    private readonly _destroyed$ = new Subject<void>();

    constructor(public appVersionService: AppVersionService) {
    }

    ngOnInit() {
        this.appVersionService.version
            .pipe(
                takeUntil(this._destroyed$),
                map((appVersion: AppVersion) => HelpToolComponent._mapAppInfo(appVersion)),
                catchError((e) => {
                    return of(HelpToolComponent._defaultValue());
                })
            )
            .subscribe(appInfo => this.appInfo = appInfo);
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    private static _defaultValue(): AppInfo {
        return {
            link: 'https://github.com/sebge2/i18n-tool/tree/master',
            version: '?',
        };
    }

    private static _mapAppInfo(appVersion: AppVersion): AppInfo {
        if (_.endsWith(appVersion.version, '-SNAPSHOT')) {
            return {
                version: appVersion.version,
                link: `https://github.com/sebge2/i18n-tool/tree/develop`
            };
        } else {
            return {
                version: appVersion.version,
                link: `https://github.com/sebge2/i18n-tool/tree/${appVersion.version}`
            };
        }
    }

}
