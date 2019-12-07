import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RepositoryInitializerComponent} from './repository-initializer.component';
import {TranslateModule} from "@ngx-translate/core";
import {RepositoryService} from "../../../translations/service/repository.service";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {BehaviorSubject} from "rxjs";
import {Repository} from "../../../translations/model/repository.model";
import {RepositoryStatus} from "../../../translations/model/repository-status.model";
import {By} from "@angular/platform-browser";
import {CoreAuthModule} from "../../../core/auth/core-auth.module";
import {AuthenticationService} from "../../../core/auth/service/authentication.service";
import {ALL_USER_ROLES, UserRole} from "../../../core/auth/model/user-role.model";
import {AuthenticatedUser} from "../../../core/auth/model/authenticated-user.model";

describe('RepositoryInitializerComponent', () => {
    let component: RepositoryInitializerComponent;
    let fixture: ComponentFixture<RepositoryInitializerComponent>;

    let repository: BehaviorSubject<Repository>;
    let repositoryService: RepositoryService;

    let user: BehaviorSubject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(new AuthenticatedUser(<AuthenticatedUser>{sessionRoles: ALL_USER_ROLES}));
    let authenticationService: AuthenticationService;

    beforeEach((() => {
        repository = new BehaviorSubject(
            new Repository(<Repository>{
                status: RepositoryStatus.NOT_INITIALIZED
            })
        );

        repositoryService = jasmine.createSpyObj('repositoryService', ['getRepository', 'initialize']);
        repositoryService.getRepository = jasmine.createSpy().and.returnValue(repository);
        repositoryService.initialize = jasmine.createSpy().and.returnValue(Promise.resolve());

        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentUser = jasmine.createSpy().and.returnValue(user);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule,
                    CoreAuthModule,
                    TranslateModule.forRoot()
                ],
                providers: [
                    {provide: RepositoryService, useValue: repositoryService},
                    {provide: AuthenticationService, useValue: authenticationService}
                ],
                declarations: [RepositoryInitializerComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(RepositoryInitializerComponent);
        component = fixture.componentInstance;
    }));

    it('should display not initialized',
        () => {
            fixture.detectChanges();

            expect(fixture.debugElement.query(By.css('.fa-times-circle'))).not.toBeNull();
        }
    );

    it('should display button',
        async () => {
            user.next(new AuthenticatedUser(<AuthenticatedUser>{sessionRoles: [UserRole.ADMIN, UserRole.MEMBER_OF_REPOSITORY]}));

            fixture.detectChanges();

            fixture.whenStable().then(() => {
                const button = fixture.debugElement.nativeElement.querySelector('#button');

                expect(button).not.toBeNull();

                button.click();

                expect(repositoryService.initialize).toHaveBeenCalled();
            });
        }
    );

    it('should display initializing',
        async () => {
            repository.next(new Repository(<Repository>{status: RepositoryStatus.INITIALIZING}));

            fixture.detectChanges();
            fixture.whenStable().then(() => {
                expect(fixture.debugElement.query(By.css('.fa-spinner'))).not.toBeNull();
            });
        }
    );

    it('should display initialized',
        async () => {
            repository.next(new Repository(<Repository>{status: RepositoryStatus.INITIALIZING}));
            repository.next(new Repository(<Repository>{status: RepositoryStatus.INITIALIZED}));

            fixture.detectChanges();
            fixture.whenStable().then(() => {
                expect(fixture.debugElement.query(By.css('.fa-check'))).not.toBeNull();
            });
        }
    );
});
