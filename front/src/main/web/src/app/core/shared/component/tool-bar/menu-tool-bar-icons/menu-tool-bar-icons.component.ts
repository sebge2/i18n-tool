import {Component, OnDestroy, OnInit} from '@angular/core';
import {ToolBarService, ToolDescriptor, ToolSelection, ToolSelectionRequest} from "@i18n-core-shared";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import * as _ from "lodash";

@Component({
    selector: 'app-menu-tool-bar-icons',
    templateUrl: './menu-tool-bar-icons.component.html',
    styleUrls: ['./menu-tool-bar-icons.component.scss']
})
export class MenuToolBarIconsComponent implements OnInit, OnDestroy {

    currentTools: ToolDescriptor[] = [];
    activeTool: ToolDescriptor | undefined;

    private readonly _destroyed$ = new Subject<void>();

    constructor(public toolBarService: ToolBarService) {
    }

    ngOnInit(): void {
        this.toolBarService
            .getActiveTool()
            .pipe(
                takeUntil(this._destroyed$),
            )
            .subscribe((activeTool: ToolSelection) =>
                this.activeTool = _.get(activeTool, 'toolDescriptor')
            );

        this.toolBarService
            .getTools()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(tools => this.currentTools = tools);
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    isActive(toolDescriptor: ToolDescriptor): boolean {
        return _.eq(
            _.get(this.activeTool, 'id'),
            _.get(toolDescriptor, 'id')
        );
    }

    onClick(toolDescriptor: ToolDescriptor): void {
        this.toolBarService.select(new ToolSelectionRequest(toolDescriptor.id));
    }
}
