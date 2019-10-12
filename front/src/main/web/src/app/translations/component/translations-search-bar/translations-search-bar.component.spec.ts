import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationsSearchBarComponent} from './translations-search-bar.component';
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {WorkspaceSelectorComponent} from "./workspace-selector/workspace-selector.component";
import {TranslationLocalesSelectorComponent} from "./translation-locales-selector/translation-locales-selector.component";
import {TranslationCriterionSelectorComponent} from "./translation-criterion-selector/translation-criterion-selector.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {TranslateModule} from "@ngx-translate/core";
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {BehaviorSubject} from "rxjs";
import {WorkspaceService} from "../../service/workspace.service";
import {Workspace} from "../../model/workspace.model";

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
                    CoreUiModule,
                    CoreEventModule,
                    TranslateModule.forRoot(),
                    HttpClientModule
                ],
                providers: [
                    {provide: WorkspaceService, useValue: workspaceService}
                ],
                declarations: [
                    TranslationsSearchBarComponent,
                    WorkspaceSelectorComponent,
                    TranslationLocalesSelectorComponent,
                    TranslationCriterionSelectorComponent
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(TranslationsSearchBarComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
