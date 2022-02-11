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
 * Request asking to translate a text.
 */
export interface TextTranslationRequestDto { 
    /**
     * Text to translate.
     */
    text: string;
    /**
     * Id of the locale in which the text is written.
     */
    fromLocaleId: string;
    /**
     * Id of the locale in which the text must be translated.
     */
    targetLocaleId: string;
}