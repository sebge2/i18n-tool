import {TestBed} from '@angular/core/testing';

import {TranslationLocalesService} from './locales-translations.service';

describe('LocalesTranslationsService', () => {
    beforeEach(() => TestBed.configureTestingModule({}));

    it('should be created', () => {
        const service: TranslationLocalesService = TestBed.get(TranslationLocalesService);
        expect(service).toBeTruthy();
    });
});
