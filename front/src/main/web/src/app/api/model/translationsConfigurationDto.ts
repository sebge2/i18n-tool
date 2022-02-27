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
import { BundleConfigurationDto } from './bundleConfigurationDto';

/**
 * Configuration of how translations are retrieved and manage for a particular repository.
 */
export interface TranslationsConfigurationDto {
  jsonIcu: BundleConfigurationDto;
  javaProperties: BundleConfigurationDto;
  /**
   * The fully-qualified names of keys to ignore.
   */
  ignoredKeys: Array<string>;
}
