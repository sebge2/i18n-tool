import {Component, OnInit} from '@angular/core';
import {ToolBarService, ToolSelection} from "@i18n-core-shared";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'app-menu-tool-bar',
    templateUrl: './menu-tool-bar.component.html',
    styleUrls: ['./menu-tool-bar.component.scss']
})
export class MenuToolBarComponent implements OnInit {

    toolSelection: ToolSelection | undefined;

    private readonly _destroyed$ = new Subject<void>();

    constructor(private _toolBarService: ToolBarService) {
    }

    ngOnInit(): void {
        this._toolBarService
            .getActiveTool()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(toolSelection => this.toolSelection = toolSelection);
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    onClose() {
        this._toolBarService.close();
    }
}
