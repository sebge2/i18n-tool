import { TranslationsPageTranslation } from './translations-page-translation.model';
import {TranslationsPageRowDto} from "../../../api/model/translationsPageRowDto";

export class TranslationsPageRow {
  static fromDto(dto: TranslationsPageRowDto): TranslationsPageRow {
    return new TranslationsPageRow(
      dto.workspace,
      dto.bundleFile,
      dto.bundleKeyId,
      dto.bundleKey,
      dto.translations.map((translation) => TranslationsPageTranslation.fromDto(translation))
    );
  }

  constructor(
    public workspace: string,
    public bundleFile: string,
    public bundleKeyId: string,
    public bundleKey: string,
    public translations: TranslationsPageTranslation[]
  ) {}
}
