import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MenuComponent} from './menu.component';
import {MaterialModule} from "../../material.module";

describe('MenuComponent', () => {
    let component: MenuComponent;
    let fixture: ComponentFixture<MenuComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                imports: [
                    MaterialModule
                ],
                declarations: [MenuComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(MenuComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
