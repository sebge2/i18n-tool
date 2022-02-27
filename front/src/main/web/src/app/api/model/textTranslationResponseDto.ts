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
import { ExternalTranslationSourceDto } from './externalTranslationSourceDto';
import { TextTranslationDto } from './textTranslationDto';

/**
 * Translations of a text. This text may have been translated by different sources.
 */
export interface TextTranslationResponseDto { 
    /**
     * Definition of external translation sources.
     */
    externalSources?: Array<ExternalTranslationSourceDto>;
    /**
     * Available translations.
     */
    translations?: Array<TextTranslationDto>;
}