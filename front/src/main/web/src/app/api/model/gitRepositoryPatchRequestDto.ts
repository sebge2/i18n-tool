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
import { RepositoryPatchRequestDto } from './repositoryPatchRequestDto';
import { TranslationsConfigurationPatchDto } from './translationsConfigurationPatchDto';

/**
 * Request asking the update of a Git repository
 */
export interface GitRepositoryPatchRequestDto extends RepositoryPatchRequestDto {
  /**
   * The name of the default branch used to find translations
   */
  defaultBranch?: string;
  /**
   * Regex specifying branches that can be scanned by this tool.
   */
  allowedBranches?: string;
  /**
   * The unique name of this repository.
   */
  name?: string;
  /**
   * Username to use to connect to the Git repository (empty means that it will be removed)
   */
  username?: string;
  /**
   * Password to connect to the Git repository (empty means that it will be removed)
   */
  password?: string;
}
export namespace GitRepositoryPatchRequestDto {}
