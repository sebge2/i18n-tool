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
import { TranslationDto } from './translationDto';

/**
 * Event sent when translations have been updated.
 */
export interface TranslationsUpdateDto {
  /**
   * Id of the editor.
   */
  userId: string;
  /**
   * Display name of the editor.
   */
  userDisplayName: string;
  /**
   * Updated translations.
   */
  translations: Array<TranslationDto>;
}
