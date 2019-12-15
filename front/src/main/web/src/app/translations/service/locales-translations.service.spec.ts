import {TestBed} from '@angular/core/testing';

import {LocalesTranslationsService} from './locales-translations.service';

describe('LocalesTranslationsService', () => {
    beforeEach(() => TestBed.configureTestingModule({}));

    it('should be created', () => {
        const service: LocalesTranslationsService = TestBed.get(LocalesTranslationsService);
        expect(service).toBeTruthy();
    });
});
