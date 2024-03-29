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

/**
 * Patch of an entry in a dictionary. This entry translate a concept in different locales.
 */
export interface DictionaryEntryPatchDto {
  /**
   * The unique id of this entry.
   */
  id: string;
  /**
   * Map associating the locale id and the translation of the related concept.
   */
  translations?: { [key: string]: string };
}
