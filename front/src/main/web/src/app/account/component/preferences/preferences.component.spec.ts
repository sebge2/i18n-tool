import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PreferencesComponent} from './preferences.component';

describe('PreferencesComponent', () => {
    let component: PreferencesComponent;
    let fixture: ComponentFixture<PreferencesComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                declarations: [PreferencesComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(PreferencesComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
