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
import { RepositoryDto } from './repositoryDto';

/**
 * Git Repository
 */
export interface GitRepositoryDto extends RepositoryDto { 
    /**
     * Location URL of this repository
     */
    location: string;
    /**
     * The name of the default branch used to find translations
     */
    defaultBranch: string;
}
export namespace GitRepositoryDto {
}