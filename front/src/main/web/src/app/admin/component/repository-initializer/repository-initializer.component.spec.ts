import {ComponentFixture, getTestBed, TestBed} from '@angular/core/testing';

import {RepositoryInitializerComponent} from './repository-initializer.component';
import {TranslateModule} from "@ngx-translate/core";
import {RepositoryService} from "../../../translations/service/repository.service";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {Repository} from "../../../translations/model/repository.model";
import {RepositoryStatus} from "../../../translations/model/repository-status.model";
import {By} from "@angular/platform-browser";

describe('RepositoryInitializerComponent', () => {
    let injector: TestBed;
    let component: RepositoryInitializerComponent;
    let fixture: ComponentFixture<RepositoryInitializerComponent>;
    let repositoryService: MockRepositoryService;

    class MockRepositoryService {

        readonly subject: Subject<Repository> = new BehaviorSubject(new Repository(<Repository>{status: RepositoryStatus.NOT_INITIALIZED}));

        public getRepository(): Observable<Repository> {
            return this.subject;
        }
    }

    beforeEach((() => {
        repositoryService = new MockRepositoryService();

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule,
                    CoreUiModule,
                    TranslateModule.forRoot()
                ],
                providers: [
                    {provide: RepositoryService, useValue: repositoryService}
                ],
                declarations: [RepositoryInitializerComponent]
            })
            .compileComponents();

        injector = getTestBed();

        fixture = TestBed.createComponent(RepositoryInitializerComponent);
        component = fixture.componentInstance;
    }));

    it('should display not initialized',
        () => {
            fixture.detectChanges();

            expect(fixture.debugElement.query(By.css('.fa-times-circle'))).not.toBeNull();
        }
    );

    it('should display initializing',
        async () => {
            repositoryService.subject.next(new Repository(<Repository>{status: RepositoryStatus.INITIALIZING}));

            fixture.detectChanges();
            fixture.whenStable().then(() => {
                expect(fixture.debugElement.query(By.css('.fa-spinner'))).not.toBeNull();
            });
        }
    );

    it('should display initialized',
        async () => {
            repositoryService.subject.next(new Repository(<Repository>{status: RepositoryStatus.INITIALIZING}));
            repositoryService.subject.next(new Repository(<Repository>{status: RepositoryStatus.INITIALIZED}));

            fixture.detectChanges();
            fixture.whenStable().then(() => {
                expect(fixture.debugElement.query(By.css('.fa-check'))).not.toBeNull();
            });
        }
    );
});
