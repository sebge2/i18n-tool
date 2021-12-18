import { TranslationsPageTranslationDto } from '../../../api';

export class TranslationsPageTranslation {
  static fromDto(dto: TranslationsPageTranslationDto): TranslationsPageTranslation {
    return new TranslationsPageTranslation(dto.originalValue, dto.updatedValue, dto.lastEditor);
  }

  constructor(public originalValue: string, public updatedValue?: string, public lastEditor?: string) {}
}
