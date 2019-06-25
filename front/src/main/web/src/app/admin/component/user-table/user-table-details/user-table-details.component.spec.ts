import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UserTableDetailsComponent} from './user-table-details.component';
import {CoreSharedModule} from "../../../../core/shared/core-shared-module";
import {UserService} from "../../../../core/auth/service/user.service";
import {TranslateModule} from "@ngx-translate/core";

describe('UserTableDetailsComponent', () => {
    let component: UserTableDetailsComponent;
    let fixture: ComponentFixture<UserTableDetailsComponent>;

    let userService: UserService;

    beforeEach(async(() => {
        userService = jasmine.createSpyObj('userService', ['getUsers', 'updateUser']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule,
                    TranslateModule.forRoot()
                ],
                providers: [
                    {provide: UserService, useValue: userService}
                ],
                declarations: [UserTableDetailsComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(UserTableDetailsComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges(); // TODO

        expect(component).toBeTruthy();
    });
});
