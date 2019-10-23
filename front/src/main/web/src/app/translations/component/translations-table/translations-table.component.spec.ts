import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {TranslationEditingCellComponent} from "./translation-editing-cell/translation-editing-cell.component";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {TranslationsTableComponent} from './translations-table.component';
import {AuthenticationService} from "../../../core/auth/service/authentication.service";
import {TranslationsService} from "../../service/translations.service";

describe('TranslationsTableComponent', () => {
    let component: TranslationsTableComponent;
    let fixture: ComponentFixture<TranslationsTableComponent>;
    let authenticationService: AuthenticationService;
    let translationsService: TranslationsService;

    beforeEach(async(() => {
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        translationsService = jasmine.createSpyObj('translationService', ['getTranslations']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule,
                    CoreSharedModule,
                    TranslateModule.forRoot()
                ],
                declarations: [
                    TranslationsTableComponent,
                    TranslationEditingCellComponent
                ],
                providers: [
                    {provide: AuthenticationService, useValue: authenticationService},
                    {provide: TranslationsService, useValue: translationsService}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(TranslationsTableComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
