import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationsSearchBarComponent} from './translations-search-bar.component';
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {TranslationCriterionSelectorComponent} from "./translation-criterion-selector/translation-criterion-selector.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {TranslateModule} from "@ngx-translate/core";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {BehaviorSubject} from "rxjs";
import {WorkspaceService} from "../../service/workspace.service";
import {Workspace} from "../../model/workspace/workspace.model";
import {TranslationLocaleSelectorComponent} from "../../../core/translation/component/translation-locale-selector/translation-locale-selector.component";
import {WorkspaceSelectorComponent} from "../../../core/translation/component/workspace-selector/workspace-selector.component";

describe('TranslationsSearchBarComponent', () => {
    let component: TranslationsSearchBarComponent;
    let fixture: ComponentFixture<TranslationsSearchBarComponent>;
    let workspaceService: WorkspaceService;
    let workspaces: BehaviorSubject<Workspace[]>;

    beforeEach(async(() => {
        workspaceService = jasmine.createSpyObj('workspaceService', ['getWorkspaces']);
        workspaces = new BehaviorSubject([]);
        workspaceService.getWorkspaces = jasmine.createSpy().and.returnValue(workspaces);

        TestBed
            .configureTestingModule({
                imports: [
                    BrowserAnimationsModule,
                    CoreSharedModule,
                    CoreEventModule,
                    TranslateModule.forRoot(),
                ],
                providers: [
                    {provide: WorkspaceService, useValue: workspaceService}
                ],
                declarations: [
                    TranslationsSearchBarComponent,
                    WorkspaceSelectorComponent,
                    TranslationLocaleSelectorComponent,
                    TranslationCriterionSelectorComponent
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(TranslationsSearchBarComponent);
        component = fixture.componentInstance;
    }));

    xit('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO issue-125
    });
});
