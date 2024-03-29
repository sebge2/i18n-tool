/**
 * i18n Tool
 * Web API of the i18n tool
 *
 * OpenAPI spec version: 1.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { TranslationsPageTranslationDto } from './translationsPageTranslationDto';

/**
 * Page in a list of paginated translations.
 */
export interface TranslationsPageRowDto {
  /**
   * Unique id of this row which is the id of the bundle key
   */
  id: string;
  /**
   * Workspace id associated to this translation.
   */
  workspace: string;
  /**
   * Bundle id associated to this translation.
   */
  bundleFile: string;
  /**
   * The id of the key for all the translations of this row
   */
  bundleKeyId: string;
  /**
   * The key for all the translations of this row
   */
  bundleKey: string;
  /**
   * All the translations of this row
   */
  translations: Array<TranslationsPageTranslationDto>;
}
