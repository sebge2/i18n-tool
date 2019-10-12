import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RepositoryInitializerComponent} from './repository-initializer.component';
import {TranslateModule} from "@ngx-translate/core";
import {RepositoryService} from "../../../translations/service/repository.service";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {BehaviorSubject} from "rxjs";
import {Repository} from "../../../translations/model/repository.model";
import {RepositoryStatus} from "../../../translations/model/repository-status.model";
import {By} from "@angular/platform-browser";

describe('RepositoryInitializerComponent', () => {
    let component: RepositoryInitializerComponent;
    let fixture: ComponentFixture<RepositoryInitializerComponent>;
    let repositoryService: RepositoryService;
    let repository: BehaviorSubject<Repository>;

    beforeEach((() => {
        repositoryService = jasmine.createSpyObj('repositoryService', ['getRepository', 'initialize']);

        repository = new BehaviorSubject(
            new Repository(<Repository>{
                status: RepositoryStatus.NOT_INITIALIZED
            })
        );

        repositoryService.getRepository = jasmine.createSpy().and.returnValue(repository);
        repositoryService.initialize = jasmine.createSpy().and.returnValue(Promise.resolve());

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
        () => {
            fixture.detectChanges();

            const button = fixture.debugElement.nativeElement.querySelector('#button');

            expect(button).not.toBeNull();

            button.click();

            expect(repositoryService.initialize).toHaveBeenCalled();
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
