import {Inject, Injectable, InjectionToken} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from "rxjs";
import {ToolDescriptor} from "../model/tool-bar/tool-descriptor.model";
import * as _ from "lodash";
import {map} from "rxjs/operators";
import {ToolSelectionRequest} from "../model/tool-bar/tool-selection-request.model";
import {ToolSelection} from "../model/tool-bar/tool-selection.model";

export const TOOL_DESCRIPTOR_TOKEN = new InjectionToken<ToolDescriptor>('tool-descriptor');

@Injectable({
    providedIn: 'root'
})
export class ToolBarService {

    private readonly _tools = new BehaviorSubject<ToolDescriptor[]>([]);
    private readonly _enabled = new BehaviorSubject<boolean>(true);
    private readonly _opened = new BehaviorSubject<boolean>(false);
    private readonly _activeTool = new BehaviorSubject<ToolSelection>(null);

    constructor(@Inject(TOOL_DESCRIPTOR_TOKEN) public descriptors: ToolDescriptor[]) {
        _.forEach(descriptors, descriptor => this.register(descriptor));
        this._initiateDefaultOpening();
    }

    getTools(): Observable<ToolDescriptor[]> {
        return this._tools.asObservable();
    }

    isEnabled(): Observable<boolean> {
        return combineLatest([this._enabled, this._tools.pipe(map(tools => _.some(tools)))])
            .pipe(map(([enabled, available]) => enabled && available));
    }

    isOpened(): Observable<boolean> {
        return this._opened.asObservable();
    }

    getActiveTool(): Observable<ToolSelection> {
        return this._activeTool.asObservable();
    }

    register(descriptor: ToolDescriptor): void {
        let descriptors = _.clone(this._tools.getValue());
        const index = this._findIndex(descriptor.id);

        if (index >= 0) {
            return;
        }

        descriptors.push(descriptor);
        descriptors = _.sortBy(descriptors, d => d.priority);

        this._tools.next(descriptors);
    }

    unregister(descriptor: ToolDescriptor): void {
        let descriptors = _.clone(this._tools.getValue());
        const index = this._findIndex(descriptor.id);

        if (index < 0) {
            return;
        }

        _.remove(descriptors, {id: descriptor.id});

        this._tools.next(descriptors);
    }

    enable(): void {
        this._enabled.next(true);
    }

    disable(): void {
        this._enabled.next(false);
    }

    open(opened: boolean = true): void {
        if (!opened) {
            this.close();
            return;
        }

        this._opened.next(true);

        this._initiateDefaultTool();
    }

    close(): void {
        this._opened.next(false);
    }

    toggle(): void {
        if (this._opened.value) {
            this.close();
        } else {
            this.open();
        }
    }

    select(request: ToolSelectionRequest): void {
        const index = this._findIndex(request.toolId);

        if (index < 0) {
            return;
        }

        this._activeTool.next(
            new ToolSelection(
                this._tools.value[index],
                request.properties
            )
        );

        this.open();
    }

    private _findIndex(toolId: string): number {
        return _.findIndex(this._tools.getValue(), d => _.eq(d.id, toolId));
    }

    private _initiateDefaultTool() {
        if (!_.isNil(this._activeTool.value)) {
            return;
        } else if (!_.some(this._tools)) {
            return;
        }

        this.select(
            new ToolSelectionRequest(_.first(this._tools.value).id)
        );
    }

    private _initiateDefaultOpening() {
        // TODO this.open();
    }
}
