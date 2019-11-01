import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UserTableComponent} from './user-table.component';
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {UserService} from "../../../core/auth/service/user.service";
import {CoreAuthModule} from "../../../core/auth/core-auth.module";
import {UserTableDetailsComponent} from "./user-table-details/user-table-details.component";
import {TranslateModule} from "@ngx-translate/core";
import {BehaviorSubject, Observable} from "rxjs";
import {User} from "../../../core/auth/model/user.model";

describe('UserTableComponent', () => {
    let component: UserTableComponent;
    let fixture: ComponentFixture<UserTableComponent>;

    let userService: UserService;
    let users: Observable<User[]>;

    beforeEach(async(() => {
        userService = jasmine.createSpyObj('userService', ['getUsers', 'updateUser']);

        users = new BehaviorSubject([]);
        userService.getUsers = jasmine.createSpy().and.returnValue(users);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule,
                    CoreAuthModule,
                    CoreUiModule,
                    TranslateModule.forRoot()
                ],
                providers: [
                    {provide: UserService, useValue: userService}
                ],
                declarations: [
                    UserTableComponent,
                    UserTableDetailsComponent
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(UserTableComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges(); // TODO

        expect(component).toBeTruthy();
    });
});
