import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MainMessageComponent} from './main-message.component';

describe('MainMessageComponent', () => {
    let component: MainMessageComponent;
    let fixture: ComponentFixture<MainMessageComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                declarations: [
                    MainMessageComponent
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(MainMessageComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
