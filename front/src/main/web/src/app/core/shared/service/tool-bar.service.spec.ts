import {fakeAsync, TestBed} from '@angular/core/testing';

import {ToolBarService} from './tool-bar.service';
import {ToolDescriptor} from "../model/tool-bar/tool-descriptor.model";
import {skip} from "rxjs/operators";

describe('ToolBarService', () => {
    let service: ToolBarService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(ToolBarService);
    });

    it('should register right order', fakeAsync(() => {
        const first = new ToolDescriptor('id1', 10, '', '', '', null);
        const second = new ToolDescriptor('id2', 0, '', '', '', null);
        const third = new ToolDescriptor('id3', 30, '', '', '', null);
        service.register(first);
        service.register(second);
        service.register(third);

        service
            .getTools()
            .subscribe(actual => {
                expect(actual).toEqual([second, first, third]);
            });
    }));

    it('should register only once', fakeAsync(() => {
        const first = new ToolDescriptor('id1', 10, '', '', '', null);
        service.register(first);
        service.register(first);

        service
            .getTools()
            .subscribe(actual => {
                expect(actual).toEqual([first]);
            });
    }));

    it('should unregister', fakeAsync(() => {
        const first = new ToolDescriptor('id1', 10, '', '', '', null);
        const second = new ToolDescriptor('id2', 0, '', '', '', null);
        const third = new ToolDescriptor('id3', 30, '', '', '', null);
        service.register(first);
        service.register(second);
        service.register(third);

        service.unregister(first);

        service
            .getTools()
            .subscribe(actual => {
                expect(actual).toEqual([second, third]);
            });
    }));

    it('should unregister allow twice', fakeAsync(() => {
        const first = new ToolDescriptor('id1', 10, '', '', '', null);
        service.register(first);
        service.unregister(first);
        service.unregister(first);

        service
            .getTools()
            .subscribe(actual => {
                expect(actual).toEqual([]);
            });
    }));
});
