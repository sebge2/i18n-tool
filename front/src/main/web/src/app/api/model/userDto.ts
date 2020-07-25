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
 * Description of the user.
 */
export interface UserDto { 
    /**
     * Id of the user.
     */
    id?: string;
    /**
     * Username of the user.
     */
    username?: string;
    /**
     * Name to display for this user (typically: first and last name).
     */
    displayName?: string;
    /**
     * Email of the user.
     */
    email?: string;
    /**
     * User roles.
     */
    roles?: Array<UserDto.RolesDtoEnum>;
    /**
     * All possible user types.
     */
    type?: UserDto.TypeDtoEnum;
}
export namespace UserDto {
    export type RolesDtoEnum = 'MEMBER_OF_ORGANIZATION' | 'MEMBER_OF_REPOSITORY' | 'ADMIN';
    export const RolesDtoEnum = {
        MEMBEROFORGANIZATION: 'MEMBER_OF_ORGANIZATION' as RolesDtoEnum,
        MEMBEROFREPOSITORY: 'MEMBER_OF_REPOSITORY' as RolesDtoEnum,
        ADMIN: 'ADMIN' as RolesDtoEnum
    };
    export type TypeDtoEnum = 'EXTERNAL' | 'INTERNAL';
    export const TypeDtoEnum = {
        EXTERNAL: 'EXTERNAL' as TypeDtoEnum,
        INTERNAL: 'INTERNAL' as TypeDtoEnum
    };
}