import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountComponent} from './preferences.component';

describe('PreferencesComponent', () => {
    let component: AccountComponent;
    let fixture: ComponentFixture<AccountComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                declarations: [AccountComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(AccountComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
